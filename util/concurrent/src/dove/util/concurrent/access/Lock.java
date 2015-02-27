package dove.util.concurrent.access;

/**
 * a simple lock that prevents multiple
 * access of methods/code parts
 */
public class Lock {
    /**
     * true, if this lock is locked
     */
    private boolean isLocked = false;

    /**
     * checks whether this lock is locked
     * and if it is, waits for the lock to unlock
     */
    public synchronized void checkLock() {
        if (isLocked)
            try {
                wait();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    /**
     * locks this lock
     */
    public void lock() {
        isLocked = true;
    }

    /**
     * releases this lock and notifys all threads waiting at checkLock
     */
    public synchronized void releaseLock() {
        isLocked = false;

        notifyAll();
    }
}
