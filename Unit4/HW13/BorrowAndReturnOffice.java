import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryMoveInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class BorrowAndReturnOffice {
    private static final BorrowAndReturnOffice instance = new BorrowAndReturnOffice();
    private final HashMap<LibraryBookId, Integer> returnedBooks = new HashMap<>();

    private BorrowAndReturnOffice() {
    }

    public static BorrowAndReturnOffice getInstance() {
        return instance;
    }

    public void receiveBook(LibraryBookId bookId) {
        returnedBooks.put(bookId, returnedBooks.getOrDefault(bookId, 0) + 1);
    }

    public void moveBooks(ArrayList<LibraryMoveInfo> moveInfos) {
        Iterator<LibraryBookId> iterator = returnedBooks.keySet().iterator();
        while (iterator.hasNext()) {
            LibraryBookId bookId = iterator.next();
            int count = returnedBooks.get(bookId);
            int orderSum = AppointmentOffice.getInstance().isOrdered(bookId);
            if (orderSum != 0) { // 送到预约处 这里涉及到预约数量和实际数量的问题 送过去的数量不能超过预约数量
                if (count > orderSum) {
                    int rest = count - orderSum;
                    for (int i = 0; i < rest; i++) {
                        BookShelf.getInstance().addBook(bookId);
                        moveInfos.add(new LibraryMoveInfo(bookId, "bro", "bs"));
                    }
                    AppointmentOffice.getInstance().addMovedBook(bookId, orderSum,
                            moveInfos, "bro");
                } else {
                    AppointmentOffice.getInstance().addMovedBook(bookId, count, moveInfos, "bro");
                }
            } else { // 送到书架
                for (int i = 0; i < count; i++) {
                    BookShelf.getInstance().addBook(bookId);
                    moveInfos.add(new LibraryMoveInfo(bookId, "bro", "bs"));
                }
            }
            iterator.remove();
        }
    }
}
