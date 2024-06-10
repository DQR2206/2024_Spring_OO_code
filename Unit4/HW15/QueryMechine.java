import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryCommand;

public class QueryMechine {
    private static final QueryMechine instance = new QueryMechine();

    private QueryMechine() {
    }

    public static QueryMechine getInstance() {
        return instance;
    }

    public void query(LibraryBookId bookId, LibraryCommand command) {
        if (bookId.isFormal()) {
            BookShelf.getInstance().queryBook(bookId, command);
        } else {
            BookDriftCorner.getInstance().queryBook(bookId, command);
        }
    }
}
