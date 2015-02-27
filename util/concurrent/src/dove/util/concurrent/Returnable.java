package dove.util.concurrent;

@FunctionalInterface
public interface Returnable<T> {
    public T run();
}
