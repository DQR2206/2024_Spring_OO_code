import java.util.ArrayList;
import java.util.HashMap;

public class RequestTable {

    // <楼层序号,每个楼层发出请求的人>
    private HashMap<Integer, ArrayList<Person>> requestMap;
    // 请求数量
    private int requestNum;
    private boolean endFlag;

    public RequestTable() {
        this.requestNum = 0;
        this.endFlag = false;
        requestMapinit();
    }

    public void requestMapinit() {
        this.requestMap = new HashMap<>();
        for (int i = 1;i <= 11;i++) {
            this.requestMap.put(i,new ArrayList<>());
        }
    }

    public synchronized void addRequest(Person person) {
        int fromFloor = person.getFromFloor();
        requestMap.get(fromFloor).add(person);
        requestNum++;
        this.notify();
    }

    public synchronized void delRequest(int floor,int index) {
        ArrayList<Person> people = requestMap.get(floor);
        people.remove(index);
        requestNum--;
    }

    public synchronized void setOver() {
        this.notify();
        this.endFlag = true;
    }

    public synchronized boolean isEmpty() {
        return (this.requestNum == 0);
    }

    public synchronized boolean isOver() {
        return this.endFlag;
    }

    public synchronized HashMap<Integer,ArrayList<Person>> getRequestMap() {
        return this.requestMap;
    }

    public synchronized void waitRequest() { // 当前请求为空 但是请求并没有结束 有新的请求或者结束时notify
        try {
            this.wait();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
