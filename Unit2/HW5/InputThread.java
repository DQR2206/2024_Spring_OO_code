import com.oocourse.elevator1.ElevatorInput;
import com.oocourse.elevator1.PersonRequest;

import java.io.IOException;
import java.util.HashMap;

public class InputThread extends Thread {

    //每一个电梯对应的请求表 1-6
    private HashMap<Integer, RequestTable> elevatorRequestTable;

    public InputThread(HashMap<Integer,RequestTable> elevatorRequestTable) {
        this.elevatorRequestTable = elevatorRequestTable;
    }

    @Override
    public void run() {
        try {
            ElevatorInput elevatorInput = new ElevatorInput(System.in);
            while (true) {
                PersonRequest request = elevatorInput.nextPersonRequest();
                if (request == null) {
                    for (Integer elevatorId : elevatorRequestTable.keySet()) {
                        elevatorRequestTable.get(elevatorId).setOver();
                    }
                    break;
                } else {
                    int fromFloor = request.getFromFloor();
                    int toFloor = request.getToFloor();
                    int personId = request.getPersonId();
                    Person person = new Person(personId, fromFloor, toFloor);
                    int elevatorId = request.getElevatorId();
                    elevatorRequestTable.get(elevatorId).addRequest(person);
                }
            }
            elevatorInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
