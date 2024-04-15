import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Strategy {
    private RequestTable requestTable;

    public Strategy(RequestTable requestTable) {
        this.requestTable = requestTable;
    }

    public Advice getAdvice(int elevatorId,int curFloor, int curNum, boolean direction,
                            HashMap<Integer, HashSet<Person>> destMap) {
        if (canOpenForout(curFloor, destMap) || canOpenForIn(curFloor, curNum, direction)) {
            return Advice.OPEN;
        }
        if (curNum != 0) {
            return Advice.MOVE;
        }
        else {
            if (requestTable.isEmpty()) { // 队列为空
                if (requestTable.isOver()) { // 输入结束
                    return Advice.OVER;
                } else {
                    return Advice.WAIT;
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

    private boolean canOpenForout(int curFloor, HashMap<Integer, HashSet<Person>> destMap) {
        return !destMap.get(curFloor).isEmpty();
    }

    // 检查当前楼层中的请求，如果有和电梯同向的请求就放进来
    private boolean canOpenForIn(int curFloor, int curNum, boolean direction) {
        if (curNum == 6) {
            return false;
        } else {
            HashMap<Integer, ArrayList<Person>> requestMap = this.requestTable.getRequestMap();
            ArrayList<Person> people = requestMap.get(curFloor);
            for (Person person : people) {
                int toFloor = person.getToFloor();
                if (toFloor > curFloor && direction) {
                    return true;
                } else if (toFloor < curFloor && !direction) {
                    return true;
                }
            }
            return false;
        }
    }

    //某请求的发出地是电梯前方的某楼层
    private boolean hasReqInOriginDirection(int curFloor, boolean direction) {
        HashMap<Integer,ArrayList<Person>> requestMap = this.requestTable.getRequestMap();
        for (Integer floor : requestMap.keySet()) {
            if (floor > curFloor && direction && !requestMap.get(floor).isEmpty()) {
                return true;
            } else if (floor < curFloor && !direction && !requestMap.get(floor).isEmpty()) {
                return true;
            }
        }
        return false;
    }

}
