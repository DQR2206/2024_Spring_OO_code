import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryCommand;
import com.oocourse.library3.LibraryMoveInfo;
import com.oocourse.library3.LibraryOpenCmd;
import com.oocourse.library3.LibraryCloseCmd;
import com.oocourse.library3.LibraryQcsCmd;
import com.oocourse.library3.LibraryReqCmd;
import com.oocourse.library3.LibraryRequest;
import static com.oocourse.library3.LibrarySystem.SCANNER;
import static com.oocourse.library3.LibrarySystem.PRINTER;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        HashMap<String, Student> students = new HashMap<>();
        Map<LibraryBookId, Integer> inventory = SCANNER.getInventory();
        BookShelf.getInstance().loadInventory(inventory);
        while (true) {
            LibraryCommand command = SCANNER.nextCommand();
            if (command == null) { break; }
            LocalDate date = command.getDate();
            AppointmentOffice.getInstance().upDateTime(date);
            if (command instanceof LibraryOpenCmd) {
                for (Student student : students.values()) {
                    student.subCredit(date);
                }
                AppointmentOffice.getInstance().setStudentList(students);
                AppointmentOffice.getInstance().subCredit();
                ArrayList<LibraryMoveInfo> moveInfos = new ArrayList<>();
                BorrowAndReturnOffice.getInstance().moveBooks(moveInfos);
                AppointmentOffice.getInstance().moveBooks(moveInfos);
                BookShelf.getInstance().moveBooks(moveInfos);
                PRINTER.move(date, moveInfos);
            } else if (command instanceof LibraryCloseCmd) {
                PRINTER.move(date, new ArrayList<>());
            } else if (command instanceof LibraryQcsCmd) {
                LibraryQcsCmd qcsCmd = (LibraryQcsCmd) command;
                String studentId = qcsCmd.getStudentId();
                if (!students.containsKey(studentId)) {
                    students.put(studentId, new Student(studentId));
                }
                Student student = students.get(studentId);
                student.queryCredit(command);
            } else {
                LibraryReqCmd request = (LibraryReqCmd) command;
                LibraryRequest.Type type = request.getType();
                LibraryBookId bookId = request.getBookId();
                String studentId = request.getStudentId();
                if (!students.containsKey(studentId)) {
                    students.put(studentId, new Student(studentId));
                }
                Student student = students.get(studentId);
                if (type == LibraryRequest.Type.BORROWED) {
                    student.borrowBook(bookId, command);
                } else if (type == LibraryRequest.Type.ORDERED) {
                    student.orderNewBook(bookId, command);
                } else if (type == LibraryRequest.Type.PICKED) {
                    student.pickBook(bookId, command);
                } else if (type == LibraryRequest.Type.QUERIED) {
                    student.queryBook(bookId, command);
                } else if (type == LibraryRequest.Type.RETURNED) {
                    student.returnBook(bookId, command);
                } else if (type == LibraryRequest.Type.RENEWED) {
                    student.renewBook(bookId, command);
                } else if (type == LibraryRequest.Type.DONATED) {
                    student.donateBook(bookId, command);
                }
            }
        }
    }
}
