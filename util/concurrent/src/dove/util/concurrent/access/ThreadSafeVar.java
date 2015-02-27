package dove.util.concurrent.access;

public class ThreadSafeVar<T> {
    protected T val;

    private boolean isSet = false;

    public ThreadSafeVar(T val) {
        this.val = val;
    }

    public ThreadSafeVar() {
        this(null);
    }

    public synchronized T getVal() {
        checkLock();

        return val;
    }

    public synchronized void setVal(T t) {
        requestLock();

        val = t;

        releaseLock();
    }

    private void requestLock() {
        isSet = true;
    }

    private synchronized void releaseLock() {
        isSet = false;

        notifyAll();
    }

    private synchronized void checkLock() {
        if (isSet) {
            try {
                wait();
            }
            catch (InterruptedException e) {
                handleException(e);
            }
        }
    }

    private void handleException(Exception e) {
        e.printStackTrace();
    }
}
