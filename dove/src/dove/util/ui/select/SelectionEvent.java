package dove.util.ui.select;

public class SelectionEvent<T> {
    public T t;

    public SelectionEvent(T t) {
        this.t = t;
    }

    public T getSelected() {
        return t;
    }
}
