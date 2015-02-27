package dove.util.ui.select;

@FunctionalInterface
public interface SearchBoxExceptionHandler {
    public void exceptionThrown(SearchBoxInternalException e);
}
