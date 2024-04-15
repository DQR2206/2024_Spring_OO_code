import com.oocourse.elevator2.ResetRequest;
import com.oocourse.elevator2.TimableOutput;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        ArrayList<RequestTable> elevatorRequestTableList = new ArrayList<>();
        RequestTable mainRequestTable = new RequestTable();
        ArrayList<AtomicReference<ResetRequest>> elevatorResets = new ArrayList<>();
        for (int i = 1;i <= 6;i++) {
            RequestTable elevatorRequestTable = new RequestTable();
            AtomicReference<ResetRequest> resetRequest = new AtomicReference<>();
            elevatorResets.add(resetRequest);
            Elevator elevator = new Elevator(i,elevatorRequestTable,mainRequestTable,resetRequest);
            elevatorRequestTableList.add(elevatorRequestTable);
            elevator.start();
        }
        Dispatcher dispatcher =
                new Dispatcher(mainRequestTable,elevatorRequestTableList,elevatorResets);
        dispatcher.start();
        InputThread inputThread = new InputThread(mainRequestTable, dispatcher);
        inputThread.start();
    }
}
