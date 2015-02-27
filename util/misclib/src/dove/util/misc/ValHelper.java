package dove.util.misc;

public class ValHelper<T> {
    private T t;

    public ValHelper(T t) {
        this.t = t;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
