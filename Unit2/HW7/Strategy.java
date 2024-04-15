import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Strategy {
    private RequestTable requestTable;
    private ArrayList<Person> buffer;

    public Strategy(RequestTable requestTable,ArrayList<Person> buffer) {
        this.requestTable = requestTable;
        this.buffer = buffer;
    }

    public Advice getAdvice(int curFloor, int transferFloor, char elevatorType,
                            int curNum, boolean direction,int capacity,
                            HashMap<Integer, HashSet<Person>> destMap) {
        if (canOpenForout(curFloor,transferFloor,elevatorType,destMap)
                || canOpenForIn(curFloor, curNum, direction, capacity)) {
            return Advice.OPEN;
        }
        if (curNum != 0) {
            return Advice.MOVE;
        }
        else {
            // 当电梯移动到换成楼层后 经历过开关门 电梯中人数一定为空
            if (buffer.isEmpty()) { // 当前的缓冲队列为空
                if (requestTable.isOver() && requestTable.isEmpty()) {
                    if (curFloor == transferFloor) {
                        return Advice.TRANSFER;
                    } else {
                        return Advice.OVER;
                    }
                } else {
                    if (curFloor == transferFloor) {
                        return Advice.TRANSFER;
                    } else {
                        return Advice.WAIT;
                    }
                }
            } else {
                if (hasReqInOriginDirection(curFloor, direction)) {
                    return Advice.MOVE;
                } else {
                    return Advice.REVERSE;
                }
            }
        }
    }

    private boolean canOpenForout(int curFloor, int transferFloor,
                                  char elevatorType, HashMap<Integer, HashSet<Person>> destMap) {
        if (!destMap.get(curFloor).isEmpty()) {
            return true;
        } else if (curFloor == transferFloor) {
            if (elevatorType == 'A') {
                for (int i = transferFloor + 1;i <= 11;i++) {
                    if (!destMap.get(i).isEmpty()) {
                        return true;
                    }
                }
            } else if (elevatorType == 'B') {
                for (int i = transferFloor - 1;i >= 1;i--) {
                    if (!destMap.get(i).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 检查当前楼层中的请求，如果有和电梯同向的请求就放进来
    private boolean canOpenForIn(int curFloor, int curNum, boolean direction, int capacity) {
        if (curNum == capacity) {
            return false;
        } else {
            for (Person person : buffer) {
                int fromFloor = person.getFromFloor();
                int toFloor = person.getToFloor();
                if (fromFloor == curFloor) {
                    if ((toFloor > curFloor && direction) || (toFloor < curFloor && !direction)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    //某请求的发出地是电梯前方的某楼层
    private boolean hasReqInOriginDirection(int curFloor, boolean direction) {
        for (Person person : buffer) {
            int fromFloor = person.getFromFloor();
            if (fromFloor > curFloor && direction || fromFloor < curFloor && !direction) {
                return true;
            }
        }
        return false;
    }

}
