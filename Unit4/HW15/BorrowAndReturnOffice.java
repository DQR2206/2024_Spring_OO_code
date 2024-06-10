import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryMoveInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class BorrowAndReturnOffice {
    private static final BorrowAndReturnOffice instance = new BorrowAndReturnOffice();
    private final HashMap<LibraryBookId, Integer> returnedFormalBooks = new HashMap<>(); // value 数量
    private final HashMap<LibraryBookId, Integer> returnedDriftBookCnt = new HashMap<>(); // value借次

    private BorrowAndReturnOffice() {
    }

    public static BorrowAndReturnOffice getInstance() {
        return instance;
    }

    public void receiveBook(LibraryBookId bookId) {
        returnedFormalBooks.put(bookId, returnedFormalBooks.getOrDefault(bookId, 0) + 1);
    }

    public void addDriftBookCnt(LibraryBookId bookId) { // 这个用来记录漂流书籍的借阅次数
        returnedDriftBookCnt.put(bookId, returnedDriftBookCnt.getOrDefault(bookId, 0) + 1);
    }

    public void moveBooks(ArrayList<LibraryMoveInfo> moveInfos) {
        Iterator<LibraryBookId> iterator = returnedFormalBooks.keySet().iterator();
        while (iterator.hasNext()) {
            LibraryBookId bookId = iterator.next();
            if (bookId.isFormal()) {
                moveFormalBook(bookId, moveInfos);
            } else {
                moveDriftBook(bookId, moveInfos);
            }
            iterator.remove();
        }
    }

    private void moveFormalBook(LibraryBookId bookId, ArrayList<LibraryMoveInfo> moveInfos) {
        int count = returnedFormalBooks.get(bookId);
        int orderSum = AppointmentOffice.getInstance().orderedNum(bookId);
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
    }

    private void moveDriftBook(LibraryBookId bookId, ArrayList<LibraryMoveInfo> moveInfos) {
        int cnt = returnedDriftBookCnt.getOrDefault(bookId, 0);
        if (cnt < 2) { // 送回漂流角
            BookDriftCorner.getInstance().addBook(bookId);
            moveInfos.add(new LibraryMoveInfo(bookId, "bro", "bdc"));
        } else { // 送到书架转正
            returnedDriftBookCnt.remove(bookId);
            BookDriftCorner.getInstance().removeBook(bookId);
            LibraryBookId.Type type = bookId.getType();
            String uid = bookId.getUid();
            type = (type == LibraryBookId.Type.BU) ? LibraryBookId.Type.B : LibraryBookId.Type.C;
            BookShelf.getInstance().addBook(new LibraryBookId(type, uid));
            moveInfos.add(new LibraryMoveInfo(bookId, "bro", "bs"));
        }
    }
}
