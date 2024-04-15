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

    public Advice getAdvice(int curFloor, int curNum, boolean direction,int capacity,
                            HashMap<Integer, HashSet<Person>> destMap) {
        if (canOpenForout(curFloor, destMap)
                || canOpenForIn(curFloor, curNum, direction, capacity)) {
            return Advice.OPEN;
        }
        if (curNum != 0) {
            return Advice.MOVE;
        }
        else {
            // 分析电梯结束的条件 我们把分配给电梯的请求划分为了两部分 buffer 和 requestTable
            // 电梯结束 : buffer为空 requestTable为空 requestTable结束 这里还是有问题
            if (buffer.isEmpty()) { // 当前的缓冲队列为空
                if (requestTable.isOver() && requestTable.isEmpty()) {
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
