import com.oocourse.library2.LibraryBookId;
import com.oocourse.library2.LibraryCommand;
import com.oocourse.library2.LibraryMoveInfo;
import com.oocourse.library2.LibraryOpenCmd;
import com.oocourse.library2.LibraryReqCmd;
import com.oocourse.library2.LibraryRequest;
import com.oocourse.library2.LibraryCloseCmd;

import static com.oocourse.library2.LibrarySystem.SCANNER;
import static com.oocourse.library2.LibrarySystem.PRINTER;

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
                ArrayList<LibraryMoveInfo> moveInfos = new ArrayList<>();
                BorrowAndReturnOffice.getInstance().moveBooks(moveInfos);
                AppointmentOffice.getInstance().moveBooks(moveInfos);
                BookShelf.getInstance().moveBooks(moveInfos);
                PRINTER.move(date, moveInfos);
            } else if (command instanceof LibraryCloseCmd) {
                PRINTER.move(date, new ArrayList<>());
            } else {
                LibraryReqCmd request = (LibraryReqCmd) command;
                LibraryRequest.Type type = request.getType();
                String studentId = request.getStudentId();
                LibraryBookId bookId = request.getBookId();
                if (!students.containsKey(studentId)) {
                    students.put(studentId, new Student(studentId));
                }
                Student student = students.get(studentId);
                if (type == LibraryRequest.Type.BORROWED) {
                    student.borrowBook(bookId, command);
                } else if (type == LibraryRequest.Type.ORDERED) {
                    student.orderBook(bookId, command);
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
