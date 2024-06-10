import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryCommand;
import com.oocourse.library3.LibraryMoveInfo;
import static com.oocourse.library3.LibrarySystem.PRINTER;

import java.util.ArrayList;
import java.util.Map;

public class BookShelf {
    private static final BookShelf instance = new BookShelf();
    private Map<LibraryBookId, Integer> inventory;

    private BookShelf() {
    }

    public static BookShelf getInstance() {
        return instance;
    }

    public void loadInventory(Map<LibraryBookId, Integer> books) {
        this.inventory = books;
    }

    public void queryBook(LibraryBookId bookId, LibraryCommand command) {
        PRINTER.info(command, this.inventory.get(bookId));
    }

    public void takeBook(LibraryBookId bookId) {
        this.inventory.put(bookId, this.inventory.get(bookId) - 1);
    }

    public void addBook(LibraryBookId bookId) {
        this.inventory.put(bookId, this.inventory.getOrDefault(bookId, 0) + 1);
    }

    public boolean canBorrowBook(LibraryBookId bookId) {
        return this.inventory.get(bookId) > 0;
    }

    public void moveBooks(ArrayList<LibraryMoveInfo> moveInfos) {
        // 从书架移向预约处
        for (LibraryBookId bookId : this.inventory.keySet()) {
            int count = this.inventory.get(bookId);
            int orderSum = AppointmentOffice.getInstance().orderedNum(bookId);
            if (count > 0 && orderSum > 0) {
                int min = Math.min(count, orderSum);
                this.inventory.put(bookId, count - min);
                AppointmentOffice.getInstance().addMovedBook(bookId, min, moveInfos, "bs");
            }
        }
    }
}
