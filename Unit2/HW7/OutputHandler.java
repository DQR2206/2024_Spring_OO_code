import com.oocourse.elevator3.TimableOutput;

public class OutputHandler {

    public static void printOpen(char elevatorType,int curFloor,int elevatorId) {
        switch (elevatorType) {
            case 'A':
                TimableOutput.println(String.format("OPEN-%d-%d-A",curFloor,elevatorId));
                break;
            case 'B':
                TimableOutput.println(String.format("OPEN-%d-%d-B",curFloor,elevatorId));
                break;
            case 'C':
                TimableOutput.println(String.format("OPEN-%d-%d",curFloor,elevatorId));
                break;
            default:
                break;
        }
    }

    public static void printClose(char elevatorType,int curFloor,int elevatorId) {
        switch (elevatorType) {
            case 'A':
                TimableOutput.println(String.format("CLOSE-%d-%d-A",curFloor,elevatorId));
                break;
            case 'B':
                TimableOutput.println(String.format("CLOSE-%d-%d-B",curFloor,elevatorId));
                break;
            case 'C':
                TimableOutput.println(String.format("CLOSE-%d-%d",curFloor,elevatorId));
                break;
            default:
                break;
        }
    }

    public static void printIn(char elevatorType,int personId,int curFloor,int elevatorId) {
        switch (elevatorType) {
            case 'A':
                TimableOutput.println(String.format("IN-%d-%d-%d-A",personId,curFloor,elevatorId));
                break;
            case 'B':
                TimableOutput.println(String.format("IN-%d-%d-%d-B",personId,curFloor,elevatorId));
                break;
            case 'C':
                TimableOutput.println(String.format("IN-%d-%d-%d",personId,curFloor,elevatorId));
                break;
            default:
                break;
        }
    }

    public static void printOut(char elevatorType,int personId,int curFloor,int elevatorId) {
        switch (elevatorType) {
            case 'A':
                TimableOutput.println(String.format("OUT-%d-%d-%d-A",personId,curFloor,elevatorId));
                break;
            case 'B':
                TimableOutput.println(String.format("OUT-%d-%d-%d-B",personId,curFloor,elevatorId));
                break;
            case 'C':
                TimableOutput.println(String.format("OUT-%d-%d-%d",personId,curFloor,elevatorId));
                break;
            default:
                break;
        }
    }

    public static void printArrive(char elevatorType,int curFloor,int elevatorId) {
        switch (elevatorType) {
            case 'A':
                TimableOutput.println(String.format("ARRIVE-%d-%d-A",curFloor,elevatorId));
                break;
            case 'B':
                TimableOutput.println(String.format("ARRIVE-%d-%d-B",curFloor,elevatorId));
                break;
            case 'C':
                TimableOutput.println(String.format("ARRIVE-%d-%d",curFloor,elevatorId));
                break;
            default:
                break;
        }
    }

    public static void printResetBegin(int elevatorId) {
        TimableOutput.println(String.format("RESET_BEGIN-%d",elevatorId));
    }

    public static void printResetEnd(int elevatorId) {
        TimableOutput.println(String.format("RESET_END-%d",elevatorId));
    }

    public static void printReceive(char elevatorType,int personId,int elevatorId) {
        switch (elevatorType) {
            case 'A':
                TimableOutput.println(String.format("RECEIVE-%d-%d-A",personId,elevatorId));
                break;
            case 'B':
                TimableOutput.println(String.format("RECEIVE-%d-%d-B",personId,elevatorId));
                break;
            case 'C':
                TimableOutput.println(String.format("RECEIVE-%d-%d",personId,elevatorId));
                break;
            default:
                break;
        }
    }

}
