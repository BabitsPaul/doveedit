package dove.util.concurrent.access;

/**
 * represents a task in this tree
 */
public final class AccessTask<R, S> {
    /**
     * gives this task low_priority
     * tasks with higher priority will be executed first
     * (tasks get ranked up with their waittime)
     */
    public static final int LOW_PRIORITY = 1;

    /**
     * normal priority
     */
    public static final int NORMAL_PRIORITY = 5;

    /**
     * gives this task high priority
     */
    public static final int HIGH_PRIORITY = 9;

    /**
     * the priority of this task
     */
    private int priority;

    /**
     * the helper correlated to this tasks op
     */
    private AccessOpHelper<R> helper;

    /**
     * the time when this task was created
     * tasks will be granted higher priority
     * depending upon their waittime
     */
    private long initializedAt;

    /**
     * specifies whether this task reads or writes
     */
    private TaskOpType opType;

    /**
     * specifies how this task will be invoked
     */
    private TaskInvokationType invokationType;

    /**
     * true, as soon as the task was executed
     */
    private boolean done = false;

    /**
     * the baseroot from which this task was started
     */
    private S caller;

    /**
     * creates a new task with the specified op
     *
     * @param r the task to execute
     */
    public AccessTask(AccessOp<R> r, TaskOpType type, TaskInvokationType invoke, S tree, ExceptionWrapper wrapper) {
        this(r, NORMAL_PRIORITY, type, invoke, tree, wrapper);
    }

    public AccessTask(AccessOp<R> r, int priority, TaskOpType type, TaskInvokationType invoke, S caller, ExceptionWrapper wrapper) {
        setPriority(priority);

        initializedAt = System.currentTimeMillis();

        opType = type;

        invokationType = invoke;

        helper = new AccessOpHelper<>(r, wrapper);

        this.caller = caller;
    }

    /**
     * @return the current priority of this task
     */
    public int getPriority() {
        return priority;
    }

    /**
     * sets the priority
     *
     * @param priority the new priority of this task
     * @throws IllegalArgumentException if an invalid value is given as priority
     */
    public void setPriority(int priority) {
        if (priority < 1 || priority > 9)
            throw new IllegalArgumentException("Invalid Priority - must be between 1 and 9");

        this.priority = priority;
    }

    /**
     * @return the time when this task was initialized
     */
    public long initializedAt() {
        return initializedAt;
    }

    /**
     * @return the task represented by this object
     */
    public AccessOpHelper<R> getTask() {
        return helper;
    }

    /**
     * @return the TaskOpType of this task
     */
    public TaskOpType getOpType() {
        return opType;
    }

    /**
     * @return the TaskInvokationType of this task
     */
    public TaskInvokationType getInvokationType() {
        return invokationType;
    }

    /**
     * @return the basenode which started this task
     */
    public S getCaller() {
        return caller;
    }

    /**
     * wait for this task to finish
     */
    public void awaitResult() {
        while (!done)
            try {
                wait();
            }
            catch (InterruptedException ignored) {
            }
    }

    /**
     * notifys any waiting thread, that this treetask was executed
     */
    public synchronized void done() {
        notifyAll();

        done = true;
    }

    public void executeTask() {
        helper.run();
    }

    /**
     * the operation type of this task
     */
    public enum TaskOpType {
        READ,
        WRITE
    }

    /**
     * specifies how this task is invokated
     */
    public enum TaskInvokationType {
        INVOKE_AND_WAIT,
        INVOKE_LATER
    }
}
