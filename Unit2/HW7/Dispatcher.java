import com.oocourse.elevator3.DoubleCarResetRequest;
import com.oocourse.elevator3.NormalResetRequest;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class Dispatcher extends Thread {

    private RequestTable mainRequestTable;
    private ArrayList<ArrayList<Elevator>> elevatorList;
    private ArrayList<ArrayList<RequestTable>> elevatorRequestTableList;
    private ArrayList<AtomicReference<NormalResetRequest>> normalElevatorResets;
    private ArrayList<AtomicReference<DoubleCarResetRequest>> doubleCarElevatorResets;

    public Dispatcher(RequestTable mainRequestTable, ArrayList<ArrayList<Elevator>> elevatorList,
                      ArrayList<ArrayList<RequestTable>> elevatorRequestTableList,
                      ArrayList<AtomicReference<NormalResetRequest>> normalElevatorResets,
                      ArrayList<AtomicReference<DoubleCarResetRequest>> doubleCarElevatorResets) {
        super("Dispatcher");
        this.mainRequestTable = mainRequestTable;
        this.elevatorList = elevatorList;
        this.normalElevatorResets = normalElevatorResets;
        this.doubleCarElevatorResets = doubleCarElevatorResets;
        this.elevatorRequestTableList = elevatorRequestTableList;
    }

    @Override
    public void run() {
        while (true) {
            if (mainRequestTable.isOver() && mainRequestTable.isEmpty()) {
                for (ArrayList<RequestTable> doubleRequestTable : this.elevatorRequestTableList) {
                    for (RequestTable requestTable : doubleRequestTable) {
                        requestTable.setOver();
                    }
                }
                break;
            }
            Person person = mainRequestTable.getOneRequestAndRemove();
            if (person == null) {
                mainRequestTable.waitRequest(); //需要wait 不然会CTLE
                continue;
            }
            dispatchRequest(person);
        }
    }

    private void dispatchRequest(Person person) {
        int fromFloor = person.getFromFloor();
        int toFloor = person.getToFloor();
        boolean direction = (toFloor - fromFloor) > 0;
        double bestScore = Integer.MIN_VALUE;
        int bestElevatorId = -1;
        int bestWaitNum = -1;
        char elevatorType = 'C';
        for (int i = 0;i < 6;i++) {
            ArrayList<Elevator> doubleElevator = this.elevatorList.get(i);
            for (Elevator elevator : doubleElevator) {
                if (direction && elevator.getLowerLimit() > fromFloor
                        || direction && elevator.getUpperLimit() <= fromFloor) {
                    continue;
                } else if (!direction && elevator.getUpperLimit() < fromFloor
                        || !direction && elevator.getLowerLimit() >= fromFloor) {
                    continue;
                }
                double score = calculateScore(elevator,person);
                if (score > bestScore) {
                    bestScore = score;
                    bestElevatorId = i;
                    bestWaitNum = elevator.getWaitNum();
                    elevatorType = elevator.getElevatorType();
                } else if (score == bestScore) {
                    if (elevator.getWaitNum() < bestWaitNum) {
                        bestScore = score;
                        bestElevatorId = i;
                        bestWaitNum = elevator.getWaitNum();
                        elevatorType = elevator.getElevatorType();
                    }
                }
            }
        }
        if (elevatorType == 'B') {
            elevatorRequestTableList.get(bestElevatorId).get(1).addRequest(person);
        } else {
            elevatorRequestTableList.get(bestElevatorId).get(0).addRequest(person);
        }
    }

    private double calculateScore(Elevator elevator,Person person) {
        int fromFloor = person.getFromFloor();
        int toFloor = person.getToFloor();
        double speed = elevator.getSpeed();
        int capacity = elevator.getCapacity();
        int curNum = elevator.getCurNum();
        int waitNum = elevator.getWaitNum();
        boolean direction = elevator.getDirection();
        int curFloor = elevator.getCurFloor();
        int distance = getDistance(fromFloor,toFloor,curFloor,direction);
        double state = getState(capacity,curNum,waitNum);
        return getScore(distance,state,speed);
    }

    private double getState(int capacity,int curNum,int waitNum) {
        return 1.3 * capacity - 1.1 * curNum - 1.0 * waitNum;
    }

    private double getScore(int distance,double state,double speed) {
        return (25 - distance + state - 5 * speed) / sqrt(speed);
    }

    private int getDistance(int fromFloor,int toFloor,int curFloor,boolean direction) {
        int distance = 0;
        int flow = (direction) ? 1 : -1;
        if ((toFloor - fromFloor) * flow > 0) { // 乘客移动方向与电梯当前移动方向相同
            if ((fromFloor - curFloor) * flow >= 0) { // 电梯沿当前方向能接到乘客
                distance = abs(fromFloor - curFloor);
            } else {
                if (flow == 1) {
                    distance = 20 - curFloor + fromFloor;
                } else {
                    distance = 20 + curFloor - fromFloor;
                }
            }
        } else {
            if (flow == 1) {
                distance = 22 - curFloor - fromFloor;
            } else {
                distance = curFloor + fromFloor - 2;
            }
        }
        return distance;
    }

    public void normalResetElevator(NormalResetRequest request) {
        int elevatorId = request.getElevatorId();
        this.normalElevatorResets.get(elevatorId - 1).set((NormalResetRequest) request);
        synchronized (this.elevatorRequestTableList.get(elevatorId - 1).get(0)) {
            this.elevatorRequestTableList.get(elevatorId - 1).get(0).notify();
        }
    }

    public void doubleCarResetElevator(DoubleCarResetRequest request) {
        int elevatorId = request.getElevatorId();
        this.doubleCarElevatorResets.get(elevatorId - 1).set((DoubleCarResetRequest) request);
        synchronized (this.elevatorRequestTableList.get(elevatorId - 1).get(0)) {
            this.elevatorRequestTableList.get(elevatorId - 1).get(0).notify();
        }
    }

    public boolean doubleCarResetOver() {
        for (AtomicReference<DoubleCarResetRequest> doubleCarResetRequest :
                this.doubleCarElevatorResets) {
            if (doubleCarResetRequest.get() != null) {
                return false;
            }
        }
        return true;
    }
}

