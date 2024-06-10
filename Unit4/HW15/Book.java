import com.oocourse.library3.LibraryBookId;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Book {
    private final LibraryBookId bookId;
    private LocalDate orderDate; // 送到预约处的日期
    private LocalDate pickedDate; // 取书/借阅日期
    private int duration; // 借阅时长
    private boolean flag; // 标记是否已经因为这本书扣过积分

    public Book(LibraryBookId bookId) {
        this.bookId = bookId;
        LibraryBookId.Type type = bookId.getType();
        switch (type) {
            case B:
                duration = 30;
                break;
            case C:
                duration = 60;
                break;
            case BU:
                duration = 7;
                break;
            case CU:
                duration = 14;
                break;
            default:
                break;
        }
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public boolean isOutOfDate(LocalDate date) {
        long daysBetween = ChronoUnit.DAYS.between(orderDate, date);
        return (daysBetween >= 5);
    }

    public LocalDate getPickedDate() {
        return pickedDate;
    }

    public LibraryBookId getBookId() {
        return bookId;
    }

    public void setPickedDate(LocalDate pickedDate) {
        this.pickedDate = pickedDate;
    }

    public void addDuration(int add) {
        duration += add;
    }

    public int getDuration() {
        return duration;
    }

    public void setFlag() {
        flag = true;
    }

    public boolean getFlag() {
        return flag;
    }
}
