package dove.util.ui.select;

public class SearchBoxInternalException
        extends RuntimeException {
    public SearchBoxInternalException(Exception cause, String msg) {
        super(msg, cause);
    }
}
