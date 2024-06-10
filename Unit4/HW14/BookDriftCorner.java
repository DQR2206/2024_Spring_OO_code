import com.oocourse.library2.LibraryBookId;
import com.oocourse.library2.LibraryCommand;
import static com.oocourse.library2.LibrarySystem.PRINTER;

import java.util.HashMap;

public class BookDriftCorner {
    private static final BookDriftCorner instance = new BookDriftCorner();
    private final HashMap<LibraryBookId, Integer> inventory = new HashMap<>();

    private BookDriftCorner() {
    }

    public static BookDriftCorner getInstance() {
        return instance;
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
    }

}