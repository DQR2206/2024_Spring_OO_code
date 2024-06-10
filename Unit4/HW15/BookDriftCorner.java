import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryCommand;
import static com.oocourse.library3.LibrarySystem.PRINTER;

import java.util.HashMap;

public class BookDriftCorner {
    private static final BookDriftCorner instance = new BookDriftCorner();
    private final HashMap<LibraryBookId, Integer> inventory = new HashMap<>();
    private final HashMap<LibraryBookId, Student> donateRecord = new HashMap<>(); // 捐献记录

    private BookDriftCorner() {
    }

    public static BookDriftCorner getInstance() {
        return instance;
    }

    public void donateBook(LibraryBookId bookId, Student student) {
        inventory.put(bookId, 1);
        donateRecord.put(bookId, student);
    }

    public void addBook(LibraryBookId bookId) {
        inventory.put(bookId, 1);
    }

    public void queryBook(LibraryBookId bookId, LibraryCommand command) {
        PRINTER.info(command, inventory.get(bookId));
    }

    public boolean canBorrowBook(LibraryBookId bookId) {
        return (inventory.get(bookId) > 0);
    }

    public void takeBook(LibraryBookId bookId) { //需要注意這裡和書架中即使拿空了也不能remove掉key
        inventory.put(bookId, 0);
    }

    public void removeBook(LibraryBookId bookId) {
        inventory.remove(bookId);
        Student student = donateRecord.get(bookId);
        student.addCredit(2);
        donateRecord.remove(bookId);
    }

}