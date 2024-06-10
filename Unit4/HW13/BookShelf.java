import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryMoveInfo;

import static com.oocourse.library1.LibrarySystem.PRINTER;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class BookShelf {
    private static final BookShelf instance = new BookShelf();
    private Map<LibraryBookId, Integer> inventory;
    private LocalDate date;

    private BookShelf() {
    }

    public static BookShelf getInstance() {
        return instance;
    }

    public void loadInventory(Map<LibraryBookId, Integer> books) {
        this.inventory = books;
    }

    public void upDateTime(LocalDate date) {
        this.date = date;
    }

    public void queryBook(LibraryBookId bookId) {
        PRINTER.info(this.date, bookId, this.inventory.get(bookId));
    }

    public void takeBook(LibraryBookId bookId) {
        this.inventory.put(bookId, this.inventory.get(bookId) - 1);
    }

    public void addBook(LibraryBookId bookId) {
        this.inventory.put(bookId, this.inventory.get(bookId) + 1);
    }

    public boolean canBorrowBook(LibraryBookId bookId) {
        return this.inventory.get(bookId) > 0;
    }

    public void moveBooks(ArrayList<LibraryMoveInfo> moveInfos) {
        // 从书架移向预约处
        for (LibraryBookId bookId : this.inventory.keySet()) {
            int count = this.inventory.get(bookId);
            int orderSum = AppointmentOffice.getInstance().isOrdered(bookId);
            if (count > 0 && orderSum > 0) {
                int min = Math.min(count, orderSum);
                this.inventory.put(bookId, count - min);
                AppointmentOffice.getInstance().addMovedBook(bookId, min, moveInfos, "bs");
            }
        }
    }
}
