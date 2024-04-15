import com.oocourse.elevator3.DoubleCarResetRequest;
import com.oocourse.elevator3.NormalResetRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Elevator extends Thread {
    private final int elevatorId; // 电梯序号 1-6
    private int curNum = 0; // 乘客人数
    private int capacity = 6; // 3 4 5 6 7 8
    private int curFloor = 1; //当前楼层 1-11
    private boolean direction = true; // 当前运行方向
    private HashMap<Integer, HashSet<Person>> destMap;
    private RequestTable mainRequestTable;
    private RequestTable requestTable;
    private ArrayList<ArrayList<RequestTable>> elevatorRequestTableList;
    private ArrayList<ArrayList<Elevator>> elevatorList;
    private AtomicReference<NormalResetRequest> normalResetRequest;
    private AtomicReference<DoubleCarResetRequest> doubleCarResetRequest;
    private ArrayList<Person> buffer; // 这是一个一个缓冲区 为reset设计的
    private Strategy strategy; // 每部电梯的策略
    private double speed = 0.4; // 0.2 0.3 0.4 0.5 0.6s
    private char elevatorType = 'C'; // 轿厢类型 A B
    private int transferFloor = -1;
    private int lowerLimit = 1;
    private int upperLimit = 11;
    private Flag busyFlag = null; // 一组电梯共享一个flag进行通信
    private AtomicInteger personSatisfied;

    public Elevator(int id, RequestTable requestTable, RequestTable mainRequestTable,
                    AtomicReference<NormalResetRequest> normalResetRequest,
                    AtomicReference<DoubleCarResetRequest> doubleCarResetRequest,
                    ArrayList<ArrayList<RequestTable>> elevatorRequestTableList,
                    ArrayList<ArrayList<Elevator>> elevatorList,
                    AtomicInteger personSatisfied) {
        super("Elevator-" + id);
        this.elevatorId = id;
        this.requestTable = requestTable;
        this.normalResetRequest = normalResetRequest;
        this.doubleCarResetRequest = doubleCarResetRequest;
        this.mainRequestTable = mainRequestTable;
        this.elevatorRequestTableList = elevatorRequestTableList;
        this.elevatorList = elevatorList;
        this.buffer = new ArrayList<>();
        this.strategy = new Strategy(requestTable,this.buffer);
        this.personSatisfied = personSatisfied;
        destMapinit();
    }

    public void destMapinit() {
        this.destMap = new HashMap<>();
        for (int i = 1;i <= 11;i++) {
            this.destMap.put(i,new HashSet<>());
        }
    }

    @Override
    public void run() {
        while (true) {
            if (this.normalResetRequest.get() != null) {
                this.normalReset();
                continue;
            }
            if (this.doubleCarResetRequest.get() != null) {
                this.doubleCarReset();
                continue;
            }
            moveRequestToBuffer();
            synchronized (mainRequestTable) {
                mainRequestTable.notifyAll();
            }
            Advice advice = strategy.getAdvice(curFloor,transferFloor,
                    elevatorType,curNum,direction,capacity,destMap);
            if (advice == Advice.OVER) {
                break;
            } else if (advice == Advice.MOVE) {
                move();
            } else if (advice == Advice.REVERSE) {
                this.direction = !this.direction;
            } else if (advice == Advice.WAIT) {
                requestTable.waitRequest();
            } else if (advice == Advice.OPEN) {
                openAndClose();
            } else if (advice == Advice.TRANSFER) {
                transfer();
            }
        }
    }

    private void moveRequestToBuffer() {
        while (!requestTable.isEmpty()) {
            Person person = requestTable.getOneRequestAndRemove();
            if (person == null) {
                continue;
            }
            OutputHandler.printReceive(elevatorType,person.getId(),elevatorId);
            buffer.add(person);
        }
    }

    private void transfer() {
        if (elevatorType == 'A' && direction) {
            this.direction = false;
        } else if (elevatorType == 'B' && !direction) {
            this.direction = true;
        }
        move();
    }

    // 先下再上
    private void openAndClose() { // 共0.4s
        OutputHandler.printOpen(elevatorType,curFloor,elevatorId);
        this.out();
        this.in();
        try {
            Thread.sleep(400);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        OutputHandler.printClose(elevatorType,curFloor,elevatorId);
    }

    // 进入电梯的条件是 是这层楼的需求 并且 移动方向一致 在进入时检查人数 迭代器删除问题
    private void in() {
        if (curNum == capacity) {
            return;
        }
        for (int i = buffer.size() - 1;i >= 0;i--) { //逆序删除
            if (curNum == capacity) {
                return;
            }
            Person person = buffer.get(i);
            int toFloor = person.getToFloor();
            int fromFloor = person.getFromFloor();
            if (fromFloor == curFloor) {
                if ((toFloor > curFloor && direction) ||
                        (toFloor < curFloor && !direction)) {
                    destMap.get(toFloor).add(person);
                    buffer.remove(person);
                    curNum++;
                    OutputHandler.printIn(elevatorType,person.getId(),curFloor,elevatorId);
                }
            }
        }
    }

    private void out() {
        // 到达目的地的人出电梯
        if (!destMap.get(curFloor).isEmpty()) {
            arriveDestOut();
        }
        // 需要换乘的人出电梯
        if (elevatorType != 'C' && curFloor == transferFloor) {
            transferOut();
        }
    }

    private void arriveDestOut() {
        HashSet<Person> people = destMap.get(this.curFloor);
        Iterator<Person> iterator = people.iterator();
        while (iterator.hasNext()) {
            Person person = iterator.next();
            iterator.remove();
            curNum--;
            OutputHandler.printOut(elevatorType,person.getId(),curFloor,elevatorId);
            this.personSatisfied.getAndIncrement();
            synchronized (mainRequestTable) {
                mainRequestTable.notifyAll();
            }
        }
    }

    private void transferOut() {
        ArrayList<Person> throwback = new ArrayList<>();
        if (elevatorType == 'A') {
            for (int i = transferFloor + 1;i <= 11;i++) {
                HashSet<Person> people = destMap.get(i);
                Iterator<Person> iterator = people.iterator();
                while (iterator.hasNext()) {
                    Person person = iterator.next();
                    Person newPerson = new Person(person.getId(),curFloor,person.getToFloor());
                    throwback.add(newPerson);
                    curNum--;
                    iterator.remove();
                    OutputHandler.printOut(elevatorType,person.getId(),curFloor,elevatorId);
                }
            }
        } else {
            for (int i = transferFloor - 1;i >= 1;i--) {
                HashSet<Person> people = destMap.get(i);
                Iterator<Person> iterator = people.iterator();
                while (iterator.hasNext()) {
                    Person person = iterator.next();
                    Person newPerson = new Person(person.getId(),curFloor,person.getToFloor());
                    throwback.add(newPerson);
                    curNum--;
                    iterator.remove();
                    OutputHandler.printOut(elevatorType,person.getId(),curFloor,elevatorId);
                }
            }
        }
        mainRequestTable.receiveResetRequest(throwback);
    }

    private void move() { // 移动一层时间为0.4s
        try {
            Thread.sleep((long) (1000 * speed));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int pace = (direction) ? 1 : -1;
        curFloor += pace;
        if (elevatorType != 'C' && curFloor == transferFloor) {
            busyFlag.setOccupied();
        }
        OutputHandler.printArrive(elevatorType,curFloor,elevatorId);
        if (elevatorType != 'C' & curFloor - pace == transferFloor) {
            busyFlag.setRelease();
        }
    }

    private void normalReset() {
        speed = normalResetRequest.get().getSpeed();
        capacity = normalResetRequest.get().getCapacity();
        if (curNum != 0) {
            removePeopleInElevator();
        }
        OutputHandler.printResetBegin(elevatorId);
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        OutputHandler.printResetEnd(elevatorId);
        mainRequestTable.receiveResetRequest(buffer);
        normalResetRequest.set(null);
    }

    private void elevatorAinit(int transferFloor,Flag flag,double speed,int capacity) {
        this.curFloor = transferFloor - 1;
        this.elevatorType = 'A';
        this.transferFloor = transferFloor;
        this.lowerLimit = 1;
        this.upperLimit = transferFloor;
        this.speed = speed;
        this.capacity = capacity;
        this.busyFlag = flag;
    }

    private void elevatorBinit(int transferFloor,Flag flag,double speed,int capacity) {
        this.curFloor = transferFloor + 1;
        this.elevatorType = 'B';
        this.transferFloor = transferFloor;
        this.lowerLimit = transferFloor;
        this.upperLimit = 11;
        this.speed = speed;
        this.capacity = capacity;
        this.busyFlag = flag;
    }

    // 创建一个新的电梯线程 
    private void doubleCarReset() {
        if (curNum != 0) {
            removePeopleInElevator();
        }
        OutputHandler.printResetBegin(elevatorId);
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        OutputHandler.printResetEnd(elevatorId);
        RequestTable newRequestTable = new RequestTable();
        Elevator newelevator = new Elevator(this.elevatorId,newRequestTable,this.mainRequestTable,
                new AtomicReference<>(), new AtomicReference<>(),
                this.elevatorRequestTableList,this.elevatorList,this.personSatisfied);
        Flag flag = new Flag();
        int transferFloor = doubleCarResetRequest.get().getTransferFloor();
        double speed = doubleCarResetRequest.get().getSpeed();
        int capacity = doubleCarResetRequest.get().getCapacity();
        newelevator.elevatorBinit(transferFloor,flag,speed,capacity);
        this.elevatorAinit(transferFloor,flag,speed,capacity);
        newelevator.start();
        this.elevatorList.get(elevatorId - 1).add(newelevator);
        this.elevatorRequestTableList.get(elevatorId - 1).add(newRequestTable);
        this.requestTable.receiveResetRequest(buffer);
        this.mainRequestTable.receiveResetRequest(this.requestTable.getRequestList());
        doubleCarResetRequest.set(null);
    }

    private void removePeopleInElevator() {
        OutputHandler.printOpen(elevatorType,curFloor,elevatorId);
        for (int i = 1;i <= 11;i++) {
            HashSet<Person> people1 = destMap.get(i);
            Iterator<Person> iterator = people1.iterator();
            while (iterator.hasNext()) {
                Person person = iterator.next();
                OutputHandler.printOut(elevatorType,person.getId(),curFloor,elevatorId);
                curNum--;
                iterator.remove();
                if (person.getToFloor() != curFloor) {
                    Person newPerson = new Person(person.getId(),
                            curFloor,person.getToFloor());
                    buffer.add(newPerson);
                } else {
                    this.personSatisfied.getAndIncrement();
                    synchronized (mainRequestTable) {
                        mainRequestTable.notifyAll();
                    }
                }
            }
        }
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        OutputHandler.printClose(elevatorType,curFloor,elevatorId);
    }

    public int getLowerLimit() {
        return lowerLimit;
    }

    public int getUpperLimit() {
        return upperLimit;
    }

    public double getSpeed() {
        return speed;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurNum() {
        return curNum;
    }

    public int getWaitNum() { // 需要注意的是等待人数为缓冲队列和等待队列中的总和
        return buffer.size() + requestTable.getRequestList().size();
    }

    public boolean getDirection() {
        return direction;
    }

    public int getCurFloor() {
        return curFloor;
    }

    public char getElevatorType() {
        return elevatorType;
    }

}
