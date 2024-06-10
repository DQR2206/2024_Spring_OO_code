import com.oocourse.library1.LibraryBookId;

public class QueryMechine {
    private static final QueryMechine instance = new QueryMechine();

    private QueryMechine() {
    }

    public static QueryMechine getInstance() {
        return instance;
    }

    public void query(LibraryBookId bookId) {
        BookShelf.getInstance().queryBook(bookId);
    }
}
