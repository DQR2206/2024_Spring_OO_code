import com.oocourse.elevator3.Request;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.NormalResetRequest;
import com.oocourse.elevator3.DoubleCarResetRequest;
import com.oocourse.elevator3.ElevatorInput;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class InputThread extends Thread {

    private RequestTable mainRequestTable; // 主请求队列 保存乘客的请求
    private Dispatcher dispatcher;
    private AtomicInteger personSatisfied; // 计算已经满足的乘客数量

    public InputThread(RequestTable mainRequestTable, Dispatcher dispatcher,
                       AtomicInteger personSatisfied) {
        super("InputThread");
        this.mainRequestTable = mainRequestTable;
        this.dispatcher = dispatcher;
        this.personSatisfied = personSatisfied;
    }

    @Override
    public void run() {
        try {
            ElevatorInput elevatorInput = new ElevatorInput(System.in);
            int personCnt = 0; // 计算乘客数量
            while (true) {
                Request request = elevatorInput.nextRequest();
                if (request == null &&
                        (personCnt == personSatisfied.get()) && (dispatcher.doubleCarResetOver())) {
                    mainRequestTable.setOver();
                    break;
                } else if (request == null) {
                    mainRequestTable.waitRequest();
                } else if (request instanceof PersonRequest) {
                    PersonRequest personRequest = (PersonRequest) request;
                    int fromFloor = personRequest.getFromFloor();
                    int toFloor = personRequest.getToFloor();
                    int personId = personRequest.getPersonId();
                    Person person = new Person(personId,fromFloor,toFloor);
                    mainRequestTable.addRequest(person);
                    personCnt++;
                } else if (request instanceof NormalResetRequest) {
                    this.dispatcher.normalResetElevator((NormalResetRequest) request);
                } else if (request instanceof DoubleCarResetRequest) {
                    this.dispatcher.doubleCarResetElevator((DoubleCarResetRequest) request);
                }
            }
            elevatorInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
