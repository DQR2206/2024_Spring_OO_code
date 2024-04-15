import com.oocourse.elevator2.ResetRequest;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Dispatcher extends Thread {

    private RequestTable mainRequestTable;
    private ArrayList<RequestTable> elevatorRequestTableList;
    private ArrayList<AtomicReference<ResetRequest>> elevatorResets;

    public Dispatcher(RequestTable mainRequestTable,
                      ArrayList<RequestTable> elevatorRequestTableList,
                      ArrayList<AtomicReference<ResetRequest>> elevatorResets) {
        super("Dispatcher");
        this.mainRequestTable = mainRequestTable;
        this.elevatorResets = elevatorResets;
        this.elevatorRequestTableList = elevatorRequestTableList;
    }

    @Override
    public void run() {
        int cnt = 0;
        while (true) {
            if (mainRequestTable.isEmpty() && mainRequestTable.isOver()) {
                for (RequestTable requestTable : this.elevatorRequestTableList) {
                    requestTable.setOver();
                }
                break;
            }
            Person person = mainRequestTable.getOneRequestAndRemove();
            if (person == null) {
                mainRequestTable.waitRequest(); //需要wait 不然会CTLE
                continue;
            }
            int elevatorId = cnt % 6;
            cnt++;
            elevatorRequestTableList.get(elevatorId).addRequest(person);
        }
    }

    public void resetElevator(ResetRequest request) {
        int elevatorId = request.getElevatorId();
        int capacity = request.getCapacity();
        double speed = request.getSpeed();
        this.elevatorResets.get(elevatorId - 1).set((ResetRequest)request);
        synchronized (this.elevatorRequestTableList.get(elevatorId - 1)) {
            this.elevatorRequestTableList.get(elevatorId - 1).notify();
        }
    }

    public boolean resetOver() {
        for (AtomicReference<ResetRequest> resetRequest : this.elevatorResets) {
            if (resetRequest.get() != null) {
                return false;
            }
        }
        return true;
    }
}

