import com.oocourse.library1.LibraryBookId;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Book {
    private final LibraryBookId bookId;
    private LocalDate orderDate; // 送到预约处的日期

    public Book(LibraryBookId bookId) {
        this.bookId = bookId;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public boolean isOutOfDate(LocalDate date) {
        long daysBetween = ChronoUnit.DAYS.between(orderDate, date);
        return (daysBetween >= 5);
    }

    public LibraryBookId getBookId() {
        return bookId;
    }
}
