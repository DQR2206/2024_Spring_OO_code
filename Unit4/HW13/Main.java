import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryCommand;
import com.oocourse.library1.LibraryMoveInfo;
import com.oocourse.library1.LibraryRequest;

import static com.oocourse.library1.LibrarySystem.SCANNER;
import static com.oocourse.library1.LibrarySystem.PRINTER;

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
            LibraryCommand<?> command = SCANNER.nextCommand();
            if (command == null) { break; }
            LocalDate date = command.getDate();
            BookShelf.getInstance().upDateTime(date);
            AppointmentOffice.getInstance().upDateTime(date);
            if (command.getCmd().equals("OPEN")) {
                ArrayList<LibraryMoveInfo> moveInfos = new ArrayList<>();
                BorrowAndReturnOffice.getInstance().moveBooks(moveInfos);
                AppointmentOffice.getInstance().moveBooks(moveInfos);
                BookShelf.getInstance().moveBooks(moveInfos);
                PRINTER.move(date, moveInfos);
            } else if (command.getCmd().equals("CLOSE")) {
                PRINTER.move(date, new ArrayList<>());
            } else {
                LibraryRequest request = (LibraryRequest) command.getCmd();
                String studentId = request.getStudentId();
                if (!students.containsKey(studentId)) {
                    students.put(studentId, new Student(studentId));
                }
                Student student = students.get(studentId);
                LibraryBookId bookId = request.getBookId();
                if (request.getType() == LibraryRequest.Type.BORROWED) {
                    student.borrowBook(bookId, date, request);
                } else if (request.getType() == LibraryRequest.Type.ORDERED) {
                    student.orderBook(bookId, date, request);
                } else if (request.getType() == LibraryRequest.Type.PICKED) {
                    student.pickBook(bookId, date, request);
                } else if (request.getType() == LibraryRequest.Type.QUERIED) {
                    student.queryBook(bookId);
                } else if (request.getType() == LibraryRequest.Type.RETURNED) {
                    student.returnBook(bookId, date, request);
                }
            }
        }
    }
}
