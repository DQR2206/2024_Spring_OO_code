import com.oocourse.elevator2.ResetRequest;
import com.oocourse.elevator2.TimableOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
    private AtomicReference<ResetRequest> resetRequest;
    private ArrayList<Person> buffer; // 这是一个一个缓冲区 为reset设计的
    private Strategy strategy; // 每部电梯的策略
    private double speed = 0.4; // 0.2 0.3 0.4 0.5 0.6s

    public Elevator(int id,RequestTable requestTable,
                    RequestTable mainRequestTable,AtomicReference<ResetRequest> resetRequest) {
        super("Elevator-" + id);
        this.elevatorId = id;
        this.requestTable = requestTable;
        this.resetRequest = resetRequest;
        this.mainRequestTable = mainRequestTable;
        this.buffer = new ArrayList<>();
        this.strategy = new Strategy(requestTable,this.buffer);
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
            if (this.resetRequest.get() != null) {
                this.reset();
                continue;
            }
            // 将调度器与电梯的共享队列中的请求移动到buffer中并输出 每一次调用后requestTable一定为空
            moveRequestToBuffer();
            synchronized (mainRequestTable) {
                mainRequestTable.notify();
            }
            Advice advice = strategy.getAdvice(curFloor,curNum,direction,capacity,destMap);
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
            }
        }
    }

    public void moveRequestToBuffer() {
        while (!requestTable.isEmpty()) {
            Person person = requestTable.getOneRequestAndRemove();
            buffer.add(person);
            TimableOutput.println(String.format("RECEIVE-%d-%d",
                    person.getId(),elevatorId));
        }
    }

    // 先下再上
    private void openAndClose() { // 共0.4s
        TimableOutput.println(String.format("OPEN-%d-%d",curFloor,elevatorId));
        this.out();
        this.in();
        try {
            Thread.sleep(400);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        TimableOutput.println(String.format("CLOSE-%d-%d",curFloor,elevatorId));
    }

    // 进入电梯的条件是 是这层楼的需求 并且 移动方向一致 在进入时检查人数 迭代器删除问题
    private void in() {
        if (curNum == capacity) {
            return;
        }
        for (int i = buffer.size() - 1;i >= 0;i--) { //逆序删除
            if (curNum == capacity) {
                break;
            }
            Person person = buffer.get(i);
            int toFloor = person.getToFloor();
            int fromFloor = person.getFromFloor();
            if (fromFloor == curFloor) {
                if ((toFloor > curFloor && direction) ||
                        (toFloor < curFloor && !direction)) {
                    buffer.remove(person);
                    destMap.get(toFloor).add(person);
                    curNum++;
                    TimableOutput.println(String.format("IN-%d-%d-%d",
                            person.getId(), curFloor, elevatorId));
                }
            }
        }
    }

    private void out() {
        HashSet<Person> people = destMap.get(this.curFloor);
        Iterator<Person> iterator = people.iterator();
        while (iterator.hasNext()) {
            Person person = iterator.next();
            iterator.remove();
            curNum--;
            TimableOutput.println(String.format("OUT-%d-%d-%d",
                    person.getId(),curFloor,elevatorId));
        }
    }

    private void move() { // 移动一层时间为0.4s
        try {
            Thread.sleep((long) (1000 * speed));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        curFloor += direction ? 1 : -1;
        TimableOutput.println(String.format("ARRIVE-%d-%d",curFloor,elevatorId));
    }

    private void reset() {
        speed = resetRequest.get().getSpeed();
        capacity = resetRequest.get().getCapacity();
        // 如果电梯中有人 需要先把人踢回总请求表
        // 这里需要注意 其实curNum中的人数就是放到 destMap中的人数
        if (curNum != 0) {
            TimableOutput.println(String.format("OPEN-%d-%d",curFloor,elevatorId));
            for (int i = 1;i <= 11;i++) {
                HashSet<Person> people1 = destMap.get(i);
                Iterator<Person> iterator = people1.iterator();
                while (iterator.hasNext()) {
                    Person person = iterator.next();
                    TimableOutput.println(String.format("OUT-%d-%d-%d",
                            person.getId(),curFloor,elevatorId));
                    curNum--;
                    iterator.remove();
                    if (person.getToFloor() != curFloor) {
                        Person newPerson = new Person(person.getId(),
                                curFloor,person.getToFloor());
                        buffer.add(newPerson);
                    }
                }
            }
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            TimableOutput.println(String.format("CLOSE-%d-%d",curFloor,elevatorId));
        }
        TimableOutput.println(String.format("RESET_BEGIN-%d",elevatorId));
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TimableOutput.println(String.format("RESET_END-%d",elevatorId));
        // 需要注意的是在输出之后扔回 避免提前receive
        requestTable.receiveResetRequest(buffer);
        resetRequest.set(null);
    }
}
