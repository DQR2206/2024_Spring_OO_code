import com.oocourse.elevator2.ElevatorInput;
import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.Request;
import com.oocourse.elevator2.ResetRequest;

import java.io.IOException;

public class InputThread extends Thread {

    private RequestTable mainRequestTable; // 主请求队列 保存乘客的请求
    private Dispatcher dispatcher;

    public InputThread(RequestTable mainRequestTable, Dispatcher dispatcher) {
        super("InputThread");
        this.mainRequestTable = mainRequestTable;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        try {
            ElevatorInput elevatorInput = new ElevatorInput(System.in);
            while (true) {
                Request request = elevatorInput.nextRequest();
                if (request == null && dispatcher.resetOver()) {
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
                } else if (request instanceof ResetRequest) {
                    this.dispatcher.resetElevator((ResetRequest) request);
                }
            }
            elevatorInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
