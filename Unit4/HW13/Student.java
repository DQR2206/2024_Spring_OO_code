import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryRequest;

import static com.oocourse.library1.LibrarySystem.PRINTER;

import java.time.LocalDate;
import java.util.HashSet;

public class Student {
    private final String studentId;
    private Book bookTypeB;
    private final HashSet<LibraryBookId> bookTypeC = new HashSet<>();

    public Student(String studentId) {
        this.studentId = studentId;
    }

    public void borrowBook(LibraryBookId bookId, LocalDate date, LibraryRequest request) {
        if (!BookShelf.getInstance().canBorrowBook(bookId) || bookId.isTypeA()) {
            PRINTER.reject(date,request);
        } else {
            // 首先从书架拿走，最后依据规则判断带走或放到借还处
            BookShelf.getInstance().takeBook(bookId);
            if (bookId.isTypeB()) {
                if (bookTypeB == null) {
                    PRINTER.accept(date,request);
                    bookTypeB = new Book(bookId);
                } else {
                    PRINTER.reject(date,request);
                    BorrowAndReturnOffice.getInstance().receiveBook(bookId);
                }
            } else {
                if (bookTypeC.contains(bookId)) {
                    PRINTER.reject(date,request);
                    BorrowAndReturnOffice.getInstance().receiveBook(bookId);
                } else {
                    PRINTER.accept(date,request);
                    bookTypeC.add(bookId);
                }
            }
        }
    }

    public void orderBook(LibraryBookId bookId, LocalDate date, LibraryRequest request) {
        if (bookId.isTypeA() || (bookId.isTypeB() && bookTypeB != null)
                || bookTypeC.contains(bookId)) {
            PRINTER.reject(date,request);
        } else { // 可以预约任意数量的B/C 这时需要在取书时注意判断取书顺序(拿走最早的)以及数量限制
            PRINTER.accept(date,request);
            AppointmentOffice.getInstance().addOrderedBook(studentId, bookId);
        }
    }

    public void pickBook(LibraryBookId bookId, LocalDate date, LibraryRequest request) {
        //可以在这里检查用户是否满足限制
        if ((bookId.isTypeB() && bookTypeB != null) || bookTypeC.contains(bookId)) {
            PRINTER.reject(date,request);
        } else {
            AppointmentOffice.getInstance().pickBook(this, bookId, date, request);
        }
    }

    public void queryBook(LibraryBookId bookId) {
        QueryMechine.getInstance().query(bookId);
    }

    public void returnBook(LibraryBookId bookId, LocalDate date, LibraryRequest request) {
        PRINTER.accept(date,request);
        if (bookId.isTypeB()) {
            bookTypeB = null;
        } else {
            bookTypeC.remove(bookId);
        }
        BorrowAndReturnOffice.getInstance().receiveBook(bookId);
    }

    public void addBook(LibraryBookId bookId) {
        if (bookId.isTypeB()) {
            bookTypeB = new Book(bookId);
        } else {
            bookTypeC.add(bookId);
        }
    }

    public String getStudentId() {
        return studentId;
    }

}
