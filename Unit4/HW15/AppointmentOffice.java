import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryCommand;
import com.oocourse.library3.LibraryMoveInfo;
import com.oocourse.library3.annotation.SendMessage;

import static com.oocourse.library3.LibrarySystem.PRINTER;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class AppointmentOffice {
    private static final AppointmentOffice instance = new AppointmentOffice();
    private LocalDate date;
    private final HashMap<String, HashSet<LibraryBookId>> orderedBooks = new HashMap<>();
    private final HashMap<String, ArrayList<Book>> movedBooks = new HashMap<>();
    private final HashMap<String, Student> students = new HashMap<>();

    private AppointmentOffice() {
    }

    public static AppointmentOffice getInstance() {
        return instance;
    }

    public void addOrderedBook(String studentId, LibraryBookId bookId) {
        if (orderedBooks.containsKey(studentId)) {
            orderedBooks.get(studentId).add(bookId);
        } else {
            HashSet<LibraryBookId> set = new HashSet<>();
            set.add(bookId);
            orderedBooks.put(studentId, set);
        }
    }

    public int orderedNum(LibraryBookId bookId) {
        int orderSum = 0;
        for (String studentId : orderedBooks.keySet()) {
            if (orderedBooks.get(studentId).contains(bookId)) {
                orderSum++;
            }
        }
        return orderSum;
    }

    public void addMovedBook(LibraryBookId bookId, int cnt,
                             ArrayList<LibraryMoveInfo> moveInfos, String src) {
        int i = 0;
        for (String studentId : orderedBooks.keySet()) {
            if (i == cnt) {
                break;
            }
            if (orderedBooks.get(studentId).contains(bookId)) {
                i++;
                Book book = new Book(bookId);
                book.setOrderDate(date);
                if (movedBooks.containsKey(studentId)) {
                    movedBooks.get(studentId).add(book);
                } else {
                    ArrayList<Book> list = new ArrayList<>();
                    list.add(book);
                    movedBooks.put(studentId, list);
                }
                moveInfos.add(new LibraryMoveInfo(bookId, src, "ao", studentId));
                orderedBooks.get(studentId).remove(bookId);
            }
        }
    }

    public void moveBooks(ArrayList<LibraryMoveInfo> moveInfos) { // 将移动到预约处的过期的书移走
        for (String studentId : movedBooks.keySet()) {
            ArrayList<Book> books = movedBooks.get(studentId);
            Iterator<Book> iterator = books.iterator();
            while (iterator.hasNext()) {
                Book book = iterator.next();
                if (book.isOutOfDate(this.date)) {
                    iterator.remove();
                    BookShelf.getInstance().addBook(book.getBookId());
                    moveInfos.add(new LibraryMoveInfo(book.getBookId(), "ao", "bs"));
                }
            }
        }
    }

    @SendMessage(from = "AppointmentOffice", to = "Student")
    public void getOrderedBook(Student student, LibraryBookId bookId,
                               LibraryCommand command) {
        String studentId = student.getStudentId();
        if (!movedBooks.containsKey(studentId) || movedBooks.get(studentId).isEmpty()) {
            PRINTER.reject(command);
        } else {
            LocalDate date = command.getDate();
            ArrayList<Book> books = movedBooks.get(studentId);
            Iterator<Book> iterator = books.iterator();
            while (iterator.hasNext()) {
                Book book = iterator.next();
                if (book.getBookId().equals(bookId)) {
                    iterator.remove();
                    student.addBook(bookId, date);
                    PRINTER.accept(command);
                    return;
                }
            }
            PRINTER.reject(command);
        }
    }

    public void upDateTime(LocalDate date) {
        this.date = date;
    }

    public boolean isUnfinishedOrder(LibraryBookId bookId) {
        for (String studentId : orderedBooks.keySet()) {
            if (orderedBooks.get(studentId).contains(bookId)) {
                return true;
            }
        }
        for (String studentId : movedBooks.keySet()) { // 每天开馆时检查过期预约 这里边不会有过期的预约
            for (Book book : movedBooks.get(studentId)) {
                if (book.getBookId().equals(bookId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasValidOrder(LibraryBookId bookId, String studentId) {
        if (bookId.isTypeB()) {
            return hasValidB(studentId);
        } else {
            return hasValidC(bookId, studentId);
        }
    }

    private boolean hasValidB(String studentId) {
        if (orderedBooks.containsKey(studentId)) {
            HashSet<LibraryBookId> ordered = orderedBooks.get(studentId);
            for (LibraryBookId id : ordered) {
                if (id.isTypeB()) {
                    return true;
                }
            }
        }
        if (movedBooks.containsKey(studentId)) {
            ArrayList<Book> moved = movedBooks.get(studentId);
            for (Book book : moved) {
                if (book.getBookId().isTypeB()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasValidC(LibraryBookId bookId, String studentId) {
        if (orderedBooks.containsKey(studentId) && orderedBooks.get(studentId).contains(bookId)) {
            return true;
        }
        if (movedBooks.containsKey(studentId)) {
            ArrayList<Book> moved = movedBooks.get(studentId);
            for (Book book : moved) {
                if (book.getBookId().equals(bookId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setStudentList(HashMap<String, Student> students) {
        this.students.putAll(students);
    }

    public void subCredit() {
        for (String studentId : movedBooks.keySet()) {
            ArrayList<Book> books = movedBooks.get(studentId);
            for (Book book : books) {
                if (book.isOutOfDate(this.date)) {
                    Student student = students.get(studentId);
                    student.addCredit(-3);
                }
            }
        }
    }
}
