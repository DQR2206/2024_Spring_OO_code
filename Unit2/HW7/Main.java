import com.oocourse.elevator3.DoubleCarResetRequest;
import com.oocourse.elevator3.NormalResetRequest;
import com.oocourse.elevator3.TimableOutput;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        ArrayList<ArrayList<Elevator>> elevatorList = new ArrayList<>();
        ArrayList<ArrayList<RequestTable>> elevatorRequestTableList = new ArrayList<>();
        RequestTable mainRequestTable = new RequestTable();
        ArrayList<AtomicReference<NormalResetRequest>> normalElevatorResets = new ArrayList<>();
        ArrayList<AtomicReference<DoubleCarResetRequest>>
                doubleCarElevatorResets = new ArrayList<>();
        AtomicInteger personSatisfied = new AtomicInteger(0); // 记录已经满足了需求的乘客的数量 用于判断是否结束
        for (int i = 1;i <= 6;i++) {
            RequestTable elevatorRequestTable = new RequestTable();
            AtomicReference<NormalResetRequest> normalResetRequest = new AtomicReference<>();
            normalElevatorResets.add(normalResetRequest);
            AtomicReference<DoubleCarResetRequest> doubleCarResetRequest = new AtomicReference<>();
            doubleCarElevatorResets.add(doubleCarResetRequest);
            ArrayList<RequestTable> doubleRequestTable = new ArrayList<>();
            doubleRequestTable.add(elevatorRequestTable);
            elevatorRequestTableList.add(doubleRequestTable);
            Elevator elevator = new Elevator(i,elevatorRequestTable,mainRequestTable,
                    normalResetRequest,doubleCarResetRequest,
                    elevatorRequestTableList,elevatorList,personSatisfied);
            ArrayList<Elevator> doubleElevator = new ArrayList<>();
            doubleElevator.add(elevator);
            elevatorList.add(doubleElevator);
            elevator.start();
        }
        Dispatcher dispatcher = new Dispatcher(mainRequestTable,elevatorList,
                elevatorRequestTableList,normalElevatorResets,doubleCarElevatorResets);
        dispatcher.start();
        InputThread inputThread = new InputThread(mainRequestTable, dispatcher, personSatisfied);
        inputThread.start();
    }
}
