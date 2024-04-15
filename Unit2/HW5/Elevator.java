import com.oocourse.elevator1.TimableOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Elevator extends Thread {
    private final int elevatorId; // 电梯序号 1-6
    private int curNum = 0; // 乘客人数 <=6
    private int curFloor = 1; //当前楼层 1-11
    private boolean direction = true; // 当前运行方向
    private HashMap<Integer, HashSet<Person>> destMap;
    private RequestTable requestTable;
    private Strategy strategy; // 每部电梯的策略

    public Elevator(int id, RequestTable requestTable) {
        this.elevatorId = id;
        this.requestTable = requestTable;
        this.strategy = new Strategy(requestTable);
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
            Advice advice = strategy.getAdvice(this.elevatorId,this.curFloor,
                    this.curNum,this.direction,this.destMap);
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

    // 先下再上
    private void openAndClose() { // 共0.4s
        TimableOutput.println(String.format("OPEN-%d-%d",this.curFloor,this.elevatorId));
        this.out();
        this.in();
        try {
            Thread.sleep(400);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        TimableOutput.println(String.format("CLOSE-%d-%d",this.curFloor,this.elevatorId));
    }

    // 进入电梯的条件是 是这层楼的需求 并且 移动方向一致 在进入时检查人数 迭代器删除问题
    private void in() {
        if (this.curNum == 6) {
            return;
        }
        ArrayList<Person> people = this.requestTable.getRequestMap().get(this.curFloor);
        for (int i = 0; i < people.size();i++) {
            Person person = people.get(i);
            int toFloor = person.getToFloor();
            if (this.curNum == 6) {
                break;
            }
            if ((toFloor > this.curFloor && this.direction)
                    || (toFloor < this.curFloor && !this.direction)) {
                this.requestTable.delRequest(this.curFloor,i);
                this.destMap.get(toFloor).add(person);
                this.curNum++;
                TimableOutput.println(String.format("IN-%d-%d-%d",
                        person.getId(), this.curFloor, this.elevatorId));
                i--;
            }
        }
    }

    private void out() {
        HashSet<Person> people = this.destMap.get(this.curFloor);
        Iterator<Person> iterator = people.iterator();
        while (iterator.hasNext()) {
            Person person = iterator.next();
            iterator.remove();
            this.curNum--;
            TimableOutput.println(String.format("OUT-%d-%d-%d",
                    person.getId(),this.curFloor,this.elevatorId));
        }
    }

    private void move() { // 移动一层时间为0.4s
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.curFloor += this.direction ? 1 : -1;
        TimableOutput.println(String.format("ARRIVE-%d-%d",this.curFloor,this.elevatorId));
    }

}
