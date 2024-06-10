import com.oocourse.library2.LibraryBookId;
import com.oocourse.library2.LibraryCommand;
import static com.oocourse.library2.LibrarySystem.PRINTER;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class Student {
    private final String studentId;
    private Book bookTypeB = null;
    private Book bookTypeBu = null;
    private final HashMap<LibraryBookId, Book> bookTypeC = new HashMap<>();
    private final HashMap<LibraryBookId, Book> bookTypeCu = new HashMap<>();

    public Student(String studentId) {
        this.studentId = studentId;
    }

    public void borrowBook(LibraryBookId bookId, LibraryCommand command) {
        if (!bookId.isFormal()) {
            borrowDriftBook(bookId, command);
        } else {
            borrowFormalBook(bookId, command);
        }
    }

    private void borrowFormalBook(LibraryBookId bookId, LibraryCommand command) {
        if (bookId.isTypeA() || !BookShelf.getInstance().canBorrowBook(bookId)) {
            PRINTER.reject(command);
        } else {
            BookShelf.getInstance().takeBook(bookId); //從書架取走 失败放到借还处
            LocalDate date = command.getDate();
            if (bookId.isTypeB()) {
                if (bookTypeB == null) {
                    PRINTER.accept(command);
                    Book book = new Book(bookId);
                    book.setPickedDate(date); // 设置借阅日期 用来判断是否超期
                    bookTypeB = book;
                } else {
                    PRINTER.reject(command);
                    BorrowAndReturnOffice.getInstance().receiveBook(bookId);
                }
            } else {
                if (bookTypeC.containsKey(bookId)) {
                    PRINTER.reject(command);
                    BorrowAndReturnOffice.getInstance().receiveBook(bookId);
                } else {
                    PRINTER.accept(command);
                    Book book = new Book(bookId);
                    book.setPickedDate(date);
                    bookTypeC.put(bookId, book);
                }
            }
        }
    }

    private void borrowDriftBook(LibraryBookId bookId, LibraryCommand command) {
        if (bookId.isTypeAU() || !BookDriftCorner.getInstance().canBorrowBook(bookId)) {
            PRINTER.reject(command);
        } else {
            BookDriftCorner.getInstance().takeBook(bookId); //從漂流角取走 失败放到借还处
            LocalDate date = command.getDate();
            if (bookId.isTypeBU()) {
                if (bookTypeBu == null) {
                    PRINTER.accept(command);
                    Book book = new Book(bookId);
                    book.setPickedDate(date);
                    bookTypeBu = book;
                } else {
                    PRINTER.reject(command);
                    BorrowAndReturnOffice.getInstance().receiveBook(bookId);
                }
            } else {
                if (bookTypeCu.containsKey(bookId)) {
                    PRINTER.reject(command);
                    BorrowAndReturnOffice.getInstance().receiveBook(bookId);
                } else {
                    PRINTER.accept(command);
                    Book book = new Book(bookId);
                    book.setPickedDate(date);
                    bookTypeCu.put(bookId, book);
                }
            }
        }
    }

    public void orderBook(LibraryBookId bookId, LibraryCommand command) {
        // 非正式图书不能被预约和续借
        if (!bookId.isFormal()) {
            PRINTER.reject(command);
        } else {
            if (bookId.isTypeA() || (bookId.isTypeB() && bookTypeB != null)
                    || bookTypeC.containsKey(bookId)) {
                PRINTER.reject(command);
            } else { // 可以预约任意数量的B/C 这时需要在取书时注意判断取书顺序(拿走最早的)以及数量限制
                PRINTER.accept(command);
                AppointmentOffice.getInstance().addOrderedBook(studentId, bookId);
            }
        }
    }

    public void pickBook(LibraryBookId bookId, LibraryCommand command) {
        if ((bookId.isTypeB() && bookTypeB != null) || bookTypeC.containsKey(bookId)) {
            PRINTER.reject(command);
        } else {
            AppointmentOffice.getInstance().pickBook(this, bookId, command);
        }
    }

    public void queryBook(LibraryBookId bookId, LibraryCommand command) {
        QueryMechine.getInstance().query(bookId, command);
    }

    public void returnBook(LibraryBookId bookId, LibraryCommand command) {
        LocalDate date = command.getDate();
        if (isOverdue(bookId, date)) {
            PRINTER.accept(command, "overdue");
        } else {
            PRINTER.accept(command, "not overdue");
        }
        if (bookId.isTypeB()) {
            bookTypeB = null;
        } else if (bookId.isTypeC()) {
            bookTypeC.remove(bookId);
        } else if (bookId.isTypeBU()) {
            bookTypeBu = null;
        } else {
            bookTypeCu.remove(bookId);
        }
        if (!bookId.isFormal()) {
            BorrowAndReturnOffice.getInstance().addDriftBookCnt(bookId); // 计数
        }
        BorrowAndReturnOffice.getInstance().receiveBook(bookId);
    }
    
    private boolean isOverdue(LibraryBookId bookId, LocalDate date) {
        LibraryBookId.Type type = bookId.getType();
        switch (type) {
            case B:
                LocalDate pickedDate = bookTypeB.getPickedDate();
                return (ChronoUnit.DAYS.between(pickedDate, date) > bookTypeB.getDuration());
            case C:
                LocalDate pickedDateC = bookTypeC.get(bookId).getPickedDate();
                return (ChronoUnit.DAYS.between(pickedDateC, date) >
                        bookTypeC.get(bookId).getDuration());
            case BU:
                LocalDate pickedDateBu = bookTypeBu.getPickedDate();
                return (ChronoUnit.DAYS.between(pickedDateBu, date) > 7);
            case CU:
                LocalDate pickedDateCu = bookTypeCu.get(bookId).getPickedDate();
                return (ChronoUnit.DAYS.between(pickedDateCu, date) > 14);
            default:
                return false;
        }
    }

    public void renewBook(LibraryBookId bookId, LibraryCommand command) { // 非正式图书不能被预约和续借
        if (bookId.isFormal()) {
            Book book = (bookId.isTypeB()) ? bookTypeB : bookTypeC.get(bookId);
            LocalDate lastDate = book.getPickedDate().plusDays(book.getDuration());
            LocalDate date = command.getDate();
            if (date.isEqual(lastDate) || date.isEqual(lastDate.minusDays(4))
                    || (date.isBefore(lastDate) && date.isAfter(lastDate.minusDays(4)))) {
                if (BookShelf.getInstance().canBorrowBook(bookId)
                        || !AppointmentOffice.getInstance().isUnfinishedOrder(bookId)) {
                    PRINTER.accept(command);
                    book.addDuration(30);
                    return;
                }
            }
            PRINTER.reject(command);
        } else {
            PRINTER.reject(command);
        }
    }

    public void donateBook(LibraryBookId bookId, LibraryCommand command) {
        PRINTER.accept(command);
        BookDriftCorner.getInstance().addBook(bookId);
    }

    public void addBook(LibraryBookId bookId, LocalDate date) { //预约取书
        if (bookId.isTypeB()) {
            Book book = new Book(bookId);
            book.setPickedDate(date);
            bookTypeB = book;
        } else {
            Book book = new Book(bookId);
            book.setPickedDate(date);
            bookTypeC.put(bookId, book);
        }
    }

    public String getStudentId() {
        return studentId;
    }

}
