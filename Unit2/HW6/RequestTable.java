import java.util.ArrayList;

public class RequestTable {

    private ArrayList<Person> requestList;
    private boolean endFlag;

    public RequestTable() {
        this.endFlag = false;
        this.requestList = new ArrayList<>();
    }

    public synchronized void addRequest(Person person) {
        this.requestList.add(person);
        this.notifyAll();
    }

    public synchronized void delRequest(Person person) {
        this.requestList.remove(person);
    }

    public synchronized void setOver() {
        this.endFlag = true;
        this.notifyAll();
    }

    public synchronized boolean isEmpty() {
        return (this.requestList.isEmpty());
    }

    public synchronized boolean isOver() {
        return this.endFlag;
    }

    public synchronized ArrayList<Person> getRequestList() {
        return this.requestList;
    }

    public synchronized void waitRequest() { // 当前请求为空 但是请求并没有结束 有新的请求或者结束时notify
        try {
            this.wait();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized Person getOneRequestAndRemove() {
        if (this.requestList.isEmpty()) {
            return null;
        }
        Person person = this.requestList.get(0);
        this.requestList.remove(0);
        this.notifyAll();
        return person;
    }

    public synchronized void receiveResetRequest(ArrayList<Person> resetRequest) {
        this.requestList.addAll(resetRequest);
        resetRequest.clear(); // 每次扔回之后需要清空
        this.notify();
    }
}
