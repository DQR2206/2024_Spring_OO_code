import com.oocourse.elevator1.TimableOutput;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        HashMap<Integer,RequestTable> elevatorRequest = new HashMap<>();
        for (int i = 1;i <= 6;i++) {
            RequestTable requestTable = new RequestTable();
            elevatorRequest.put(i,requestTable);
            Elevator elevator = new Elevator(i,requestTable);
            elevator.start();
        }
        Thread inputThread = new InputThread(elevatorRequest);
        inputThread.start();
    }
}
