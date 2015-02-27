package dove.util.concurrent;

public interface BackgroundTask
        extends Runnable {
    public default boolean finished() {
        return false;
    }
}
