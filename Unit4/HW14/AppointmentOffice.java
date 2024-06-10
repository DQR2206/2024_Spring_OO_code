import com.oocourse.library2.LibraryBookId;
import com.oocourse.library2.LibraryCommand;
import com.oocourse.library2.LibraryMoveInfo;

import static com.oocourse.library2.LibrarySystem.PRINTER;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class AppointmentOffice {
    private static final AppointmentOffice instance = new AppointmentOffice();
    private LocalDate date;
    private final HashMap<String, HashMap<LibraryBookId, Integer>> orderedBooks = new HashMap<>();
    private final HashMap<String, ArrayList<Book>> movedBooks = new HashMap<>();

    private AppointmentOffice() {
    }

    public static AppointmentOffice getInstance() {
        return instance;
    }

    public void addOrderedBook(String studentId, LibraryBookId bookId) {
        if (orderedBooks.containsKey(studentId)) {
            orderedBooks.get(studentId).put(bookId,
                    orderedBooks.get(studentId).getOrDefault(bookId, 0) + 1);
        } else {
            HashMap<LibraryBookId, Integer> map = new HashMap<>();
            map.put(bookId, 1);
            orderedBooks.put(studentId, map);
        }
    }

    public int orderedNum(LibraryBookId bookId) {
        int orderSum = 0;
        for (String studentId : orderedBooks.keySet()) {
            if (orderedBooks.get(studentId).containsKey(bookId)) {
                orderSum += orderedBooks.get(studentId).get(bookId);
            }
        }
        return orderSum;
    }

    public void addMovedBook(LibraryBookId bookId, int cnt,
                             ArrayList<LibraryMoveInfo> moveInfos, String src) {
        int books = cnt;
        while (books > 0) {
            for (String studentId : orderedBooks.keySet()) {
                if (orderedBooks.get(studentId).containsKey(bookId)) {
                    if (orderedBooks.get(studentId).get(bookId) != 0) {
                        if (orderedBooks.get(studentId).get(bookId) == 1) {
                            orderedBooks.get(studentId).remove(bookId);
                        } else {
                            orderedBooks.get(studentId).put(bookId,
                                    orderedBooks.get(studentId).get(bookId) - 1);
                        }
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
                        books--;
                        if (books == 0) {
                            break;
                        }
                    }
                }
            }
        }
    }

    public void moveBooks(ArrayList<LibraryMoveInfo> moveInfos) { // 将移动到预约处的过期的书移走
        for (String studentId : orderedBooks.keySet()) {
            if (!movedBooks.containsKey(studentId) || movedBooks.get(studentId).isEmpty()) {
                continue;
            }
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

    public void pickBook(Student student, LibraryBookId bookId,
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
            if (orderedBooks.get(studentId).containsKey(bookId)
                    && orderedBooks.get(studentId).get(bookId) > 0) {
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
}
