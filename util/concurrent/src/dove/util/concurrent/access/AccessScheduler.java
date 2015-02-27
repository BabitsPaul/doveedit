package dove.util.concurrent.access;

import java.util.*;
import java.util.function.Predicate;

import static dove.util.concurrent.access.AccessTask.TaskOpType.READ;


/**
 * scheduls access on a nonthreadsafe resource
 * and provides threadsafety + enhanced efficiency in comparisson
 * to a lock
 * <p>
 * and handle the priority of these tasks
 * <p>
 * this scheduler provides the option of multiple reading
 * accesses at once, and maximum one writing access at once
 * (all other operations are stoped for the time of a writing operation)
 */
public class AccessScheduler {
    /**
     * comparator for treetasks
     * <p>
     * used to update the waiting queues and determine,
     * which task should be executed first
     */
    private static final Comparator<AccessTask> comp                = (t1, t2) -> {
        int deltaPriority = t2.getPriority() - t1.getPriority();

        if (deltaPriority != 0)
            return deltaPriority;
        else
            return (int) (t2.initializedAt() - t1.initializedAt());
    };
    /**
     * the lock for quitScheduler
     * <p>
     * this lock is used whenever the quitScheduler-variable
     * is accessed
     */
    private final        Object                 quitSchedulerLock   = new Object();
    /**
     * the lock for awaiting a task
     * <p>
     * this lock is used whenever the scheduler waits for
     * new tasks, or a new task is incoming
     */
    private final        Object                 awaitTaskLock       = new Object();
    /**
     * the lock for transferring transferlocks
     * <p>
     * used to transfer the transferHandler
     * threadsafe
     */
    private final        Object                 transferHandlerLock = new Object();
    /**
     * the lock for transferring objects
     * <p>
     * this lock is used whenever on of the transfertasks
     * operations is called to signalize the scheduler to
     * pause execution
     */
    private              TransferLock           transferLock        = new TransferLock();
    /**
     * list of tasks waiting to read the tree
     */
    private              List<AccessTask>       readingWaitList     =
            Collections.synchronizedList(new ArrayList<>());
    /**
     * list of tasks waiting to write to the tree
     */
    private              List<AccessTask>       writingWaitList     =
            Collections.synchronizedList(new ArrayList<>());
    /**
     * list of tasks to execute immedeatly
     */
    private              List<AccessTask>       executeNext         =
            Collections.synchronizedList(new ArrayList<>());
    /**
     * the current state of the scheduler
     */
    private              State                  currentState        = State.IDLE;
    /**
     * the writelock of this accessscheduler
     */
    private WriteLock writeLock;
    /**
     * if this flag is true, the scheduler will finish
     * all queued tasks and stop, no further tasks will be
     * accepted as soon as this flag is set true
     */
    private boolean quitScheduler = false;
    /**
     * the number of threads maximum allowed
     * to work on tasks in parralel
     */
    private int          maxThreadCount;
    /**
     * all lockhelpers currently running in this scheduler
     */
    private LockHelper[] lockHelpers;
    /**
     * counter for updating the queue,
     * queue will only be updated on every
     * call where queueUpdateCounter % (maxThreadCount * 2) == 0
     */
    private int queueUpdateCounter = 0;

    /**
     * creates a new scheduler
     * with maximumThreadCount of 5
     */
    public AccessScheduler() {
        this(5);
    }

    /**
     * creates a new scheduler
     * with the specified threadCount
     *
     * @param maximumThreadCount the maximumThreadCount of this thread
     */
    public AccessScheduler(int maximumThreadCount) {
        maxThreadCount = maximumThreadCount;

        lockHelpers = new LockHelper[maxThreadCount];

        writeLock = new WriteLock();

        //create and launch all lockhelpers (threads)
        for (int i = 0; i < maxThreadCount; i++)
            lockHelpers[i] = new LockHelper(i);
    }

    /**
     * the scheduler will quit after this method is called
     * as soon as all remaining tasks have been executed
     * no further tasks will be accepted after this method was called
     */
    public void quitScheduler() {
        synchronized (quitSchedulerLock) {
            quitScheduler = true;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////
    // quit scheduler
    ///////////////////////////////////////////////////////////////////////////////

    /**
     * @return true, if this scheduler is about to be quit
     */
    private boolean quitSchedulerOn() {
        synchronized (quitSchedulerLock) {
            return quitScheduler;
        }
    }

    /**
     * @return the maximum number of parralel scheduler tasks
     */
    public int getMaxThreadCount() {
        return maxThreadCount;
    }

    ///////////////////////////////////////////////////////////////////////////////
    // threadcount
    ///////////////////////////////////////////////////////////////////////////////

    /**
     * set the maximumthreadcount to a new value
     * the number of threads will be updated to match the new value
     *
     * @param nThreadCount the new threadcount
     */
    public void setMaxThreadCount(int nThreadCount) {
        int oldVal = maxThreadCount;

        maxThreadCount = nThreadCount;

        //if the number of lockhelpers is reduced,
        //quit all lockhelpers with an id > nThreadCount
        for (int i = nThreadCount - 1; i < oldVal; i++)
            lockHelpers[i].quit();

        //apply the size of lockHelpers to nThreadCount
        lockHelpers = Arrays.copyOf(lockHelpers, nThreadCount);

        //if the number o lockhelpers is increased,
        //start new lockhelpers to match the new number of lockhelpers
        for (int i = oldVal - 1; i < nThreadCount - 1; i++) {
            lockHelpers[i] = new LockHelper(i);
        }
    }

    /**
     * @return the current state of this scheduler
     */
    public State getCurrentState() {
        return currentState;
    }

    ///////////////////////////////////////////////////////////////////////////////
    // state
    ///////////////////////////////////////////////////////////////////////////////

    /**
     * imports all tasks matching p from scheduler to
     * this scheduler
     *
     * @param scheduler the scheduler from which tasks are imported
     * @param p         the predicate to match tasks
     */
    public void importTasks(AccessScheduler scheduler, Predicate<AccessTask> p) {
        //create new lock
        transferLock = new TransferLock();

        //give transferlock to other scheduler
        scheduler.handTransferLock(transferLock);

        //wait till both schedulers are ready
        transferLock.awaitTransfer();

        //transfer from readingqueue of scheduler
        scheduler.readingWaitList.forEach(t ->
        {
            if (p.test(t))
                readingWaitList.add(t);
        });
        scheduler.readingWaitList.removeIf(p);

        //transfer from writingqueue of scheduler
        scheduler.writingWaitList.forEach(t ->
        {
            if (p.test(t))
                writingWaitList.add(t);
        });
        scheduler.writingWaitList.removeIf(p);

        transferLock.transferDone();
    }

    ///////////////////////////////////////////////////////////////////////////////
    // transfer tasks
    ///////////////////////////////////////////////////////////////////////////////

    /**
     * exports all tasks matching p from this scheduler to scheduler
     *
     * @param scheduler the scheduler to which the tasks are transfered
     * @param p         the predicate to match the tasks
     */
    public void exportTasks(AccessScheduler scheduler, Predicate<AccessTask> p) {
        //create new lock
        handTransferLock(new TransferLock());

        //give transferlock to other scheduler
        scheduler.handTransferLock(transferLock);

        //wait till both schedulers are ready
        getTransferLock().awaitTransfer();

        //transfer from readingqueue to scheduler
        readingWaitList.forEach(t ->
        {
            if (p.test(t))
                scheduler.readingWaitList.add(t);
        });
        readingWaitList.removeIf(p);

        //transfer from writingqueue to scheduler
        writingWaitList.forEach(t ->
        {
            if (p.test(t))
                scheduler.writingWaitList.add(t);
        });
        writingWaitList.removeIf(p);

        transferLock.transferDone();
    }

    /**
     * transfers a transferlock correspondant to a
     * transfer to this scheduler
     *
     * @param lock the new transferlock
     */
    private void handTransferLock(TransferLock lock) {
        //if currently a transfer is running
        //await the end of the transfer before switching to a new transfer
        if (getTransferLock().awaitingTransfer())
            getTransferLock().awaitTransferDone();

        synchronized (transferHandlerLock) {
            this.transferLock = lock;
        }
    }

    /**
     * @return the tasktransferlock currently held by this scheduler
     */
    private TransferLock getTransferLock() {
        synchronized (transferHandlerLock) {
            return transferLock;
        }
    }

    /**
     * executes r as soon as possible
     * if type is READ, as soon as a thread is free, or, if
     * type is WRITE, as soon as all threads are locked
     * InvokationType is INVOKE_LATER
     *
     * @param r       the task to execute
     * @param type    the type of the task
     * @param s       the source of the task
     * @param <R>     the typeparameter of the result
     * @param wrapper the exceptionwrapper correlated to this task
     * @return an accesstask representing this operation
     */
    public <R, S> AccessTask<R, S> invokeNext(AccessOp<R> r, AccessTask.TaskOpType type, S s, ExceptionWrapper wrapper) {
        AccessTask<R, S> task = new AccessTask<>(r, type, AccessTask.TaskInvokationType.INVOKE_LATER, s, wrapper);

        executeNext.add(task);

        nextTaskAvailable();

        return task;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // invokation
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * creates a new task and awaits its execution
     * the task is given normal priority
     *
     * @param r       the task to be executed
     * @param type    the type of the task
     * @param s       the basenode of the started task
     * @param wrapper the exceptionwrapper correlated to this object
     * @return the treetask representing this task
     */
    public <R, S> AccessTask<R, S> invokeAndWait(AccessOp<R> r, AccessTask.TaskOpType type, S s, ExceptionWrapper wrapper) {
        return invokeAndWait(r, type, AccessTask.NORMAL_PRIORITY, s, wrapper);
    }

    /**
     * creates a new TreeTask and waits until the task is finished
     *
     * @param r        the task to execute
     * @param type     the type of the task
     * @param priority the priority of the task
     * @param s        the basenode of the started task
     * @param wrapper  the exceptionwrapper correlated to this task
     * @return the TreeTask representing this task
     */
    public <R, S> AccessTask<R, S> invokeAndWait(AccessOp<R> r, AccessTask.TaskOpType type, int priority, S s, ExceptionWrapper wrapper) {
        if (quitSchedulerOn())
            throw new IllegalStateException("this scheduler is about to quit - no more tasks are accepted");

        AccessTask<R, S> task = new AccessTask<>(r, priority, type, AccessTask.TaskInvokationType.INVOKE_AND_WAIT, s, wrapper);

        if (type == AccessTask.TaskOpType.READ)
            readingWaitList.add(task);
        else
            writingWaitList.add(task);

        nextTaskAvailable();

        task.awaitResult();

        return task;
    }

    /**
     * creates a new TreeTask
     * and lines it up in the waitingqueue
     * <p>
     * the task has normal priority
     *
     * @param r       the task to execute
     * @param type    the type of the task
     * @param s       the basenode of the started task
     * @param wrapper the wrapper correlated to this object
     * @return a treetask representing this task
     */
    public <R, S> AccessTask<R, S> invokeLater(AccessOp<R> r, AccessTask.TaskOpType type, S s, ExceptionWrapper wrapper) {
        return invokeLater(r, type, AccessTask.NORMAL_PRIORITY, s, wrapper);
    }

    /**
     * creates a new TreeTask
     * and lines it up in the waitingqueue
     *
     * @param r        the task to execute
     * @param type     the type of the task
     * @param priority the priority of the task
     * @param s        the basenode of the started task
     * @param wrapper  the exceptionwrapper correlated to this task
     * @return a treetask representing the given task
     */
    public <R, S> AccessTask<R, S> invokeLater(AccessOp r, AccessTask.TaskOpType type, int priority, S s, ExceptionWrapper wrapper) {
        if (quitSchedulerOn())
            throw new IllegalStateException("this scheduler is about to quit - no more tasks are accepted");

        AccessTask<R, S> task = new AccessTask<>(r, priority, type, AccessTask.TaskInvokationType.INVOKE_LATER, s, wrapper);

        if (type == AccessTask.TaskOpType.READ)
            readingWaitList.add(task);
        else
            writingWaitList.add(task);

        nextTaskAvailable();

        return task;
    }

    /**
     * @return the next tasks that should be executed
     */
    private AccessTask nextTask() {
        //while there are no tasks to execute, wait for new incoming tasks
        while (writingWaitList.isEmpty() && readingWaitList.isEmpty())
            awaitTask();

        currentState = State.QUEUEOP;

        //check executeNextTaskList
        if (!executeNext.isEmpty())
            return executeNext.remove(0);

        //if the waitingqueue for reading tasks is empty, execute the first writing task
        if (readingWaitList.isEmpty()) {
            return writingWaitList.remove(0);
        }
        //if no task waits to write, execute the reading tasks
        else if (writingWaitList.isEmpty()) {
            return readingWaitList.remove(0);
        }
        //there are both tasks waiting to read and write
        //find the task with highest priority and longer waiting time
        //and execute it
        else {
            AccessTask write_highest = writingWaitList.get(0);
            AccessTask read_highest = readingWaitList.get(0);

            int c = comp.compare(write_highest, read_highest);

            if (c < 0) {
                return readingWaitList.remove(0);
            }
            if (c > 0) {
                return writingWaitList.remove(0);
            }

            //if no task with highest priority can be found, execute the reading task first
            return writingWaitList.remove(0);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // waiting queue helper methods
    ////////////////////////////////////////////////////////////////////////////

    /**
     * updates the prioritys of tasks
     * <p>
     * tasks that have waited for more than waitingtime = 100ms * (MAX_PRIORITY + 1 - tasks priority),
     * will be granted a higher priority, until they reach the maximum priority
     * <p>
     * afterwards, the waitingqueues will be updated
     * to match the new priorityvalues
     */
    private void recalculatePriorities() {
        //only update on every #maxThreadCount * 2
        //call to save resources and ensure threadsafety
        if (++queueUpdateCounter % (maxThreadCount * 2) != 0)
            return;

        //get the current systemtime
        long currentTime = System.currentTimeMillis();

        //update the priority of all tasks in
        //the writingqueue
        writingWaitList.forEach(task -> {
            int priority = task.getPriority();

            long age = currentTime - task.initializedAt();

            for (int i = 1; i < 10; i++)
                if (age > i * 100 && 10 - priority > i)
                    task.setPriority(priority + 1);
        });

        //update the priority of all tasks in
        //the reading queue
        readingWaitList.forEach(task -> {
            int priority = task.getPriority();

            long age = currentTime - task.initializedAt();

            for (int i = 1; i < 10; i++)
                if (age > i * 100 && 10 - priority > i)
                    task.setPriority(priority + 1);
        });

        //comparator used to sort the queue for priority
        //tasks with higher priority get a better place in the
        //waiting queue, and older tasks are executed before newer ones
        //with the same priority
        //update queues to match the new prioritys
        Collections.sort(writingWaitList, comp);
        Collections.sort(readingWaitList, comp);
    }

    /**
     * lets the scheduler wait for the next incoming task
     */
    private void awaitTask() {
        currentState = State.IDLE;

        try {
            synchronized (awaitTaskLock) {
                wait();
            }
        }
        catch (InterruptedException ignored) {
        }
    }

    /**
     * notifys the scheduler that a new task is available
     */
    private void nextTaskAvailable() {
        synchronized (awaitTaskLock) {
            notify();
        }
    }

    /**
     * the current state of the scheduler
     */
    public enum State {
        /**
         * the scheduler is idle and awaiting new tasks
         */
        IDLE,

        /**
         * the scheduler currently performs a reading operation
         */
        READ,

        /**
         * the scheduler currently performs a writing operation
         */
        WRITE,

        /**
         * the scheduler currently works on the waiting queues
         */
        QUEUEOP
    }

    //////////////////////////////////////////////////////////////////////////////
    // waitingqueue handler
    //////////////////////////////////////////////////////////////////////////////

    /**
     * runs incoming tasks, and sorts them according
     * to their (updated) priority
     */
    private class LockHelper
            implements Runnable {
        private boolean quit = false;

        /**
         * creates a new lockhelper with the specified threadnum
         * and starts the related thread
         *
         * @param threadNum the id of this lockhelper
         */
        public LockHelper(int threadNum) {
            Thread t = new Thread(this, "lockhelper thread #" + threadNum);
            t.setDaemon(true);
            t.start();
        }

        /**
         * quits this lockhelper
         */
        public void quit() {
            quit = true;
        }

        public void run() {
            //as long as the scheduler/lockhelper is not quited
            //or the scheduler is quited and there are tasks available, process
            //these tasks
            while ((!quitSchedulerOn() && !quit) ||
                    (quitSchedulerOn() && !(writingWaitList.isEmpty() && readingWaitList.isEmpty()))) {
                //check whether this scheduler is awaiting any transfers
                //and if the scheduler is awaiting any transfers wait until these are done
                if (getTransferLock().awaitingTransfer())
                    getTransferLock().awaitTransferDone();

                //check whether the writelock is locked and
                //and wait if the lock is enabled
                writeLock.checkWriteLock();

                recalculatePriorities();

                executeTask();
            }

            //scheduler is quited
            //execute remaining tasks and quit
            while (!(writingWaitList.isEmpty() && readingWaitList.isEmpty()))
                executeTask();
        }

        /**
         * execute the next task in the waiting queue
         */
        private void executeTask() {
            AccessTask t = nextTask();

            if (t.getOpType().equals(READ)) {
                //execute task
                t.executeTask();

                //notify listeners that the task is done
                t.done();
            }
            else {
                //only one writingtask at once allowed (no parralel executed tasks)
                //--> wait for other currently running tasks to wait
                //for this task to read
                writeLock.requestWriteLock();

                //run the task
                t.executeTask();

                //notify listeners that the task is finished
                t.done();

                //release the writelock
                writeLock.releaseWriteLock();
            }
        }
    }

    ////////////////////////////////////////////////////////////////////
    // transferlock
    ////////////////////////////////////////////////////////////////////

    private class TransferLock {
        private final Object  transferAwaitLock = new Object();
        private final Object  transferReadyLock = new Object();
        private       boolean awaitingTransfer  = false;
        private       int     schedulersReady   = 0;

        /**
         * wait to start the transfer
         */
        public void awaitTransfer() {
            awaitingTransfer = true;

            synchronized (transferReadyLock) {
                try {
                    wait();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * called if both schedulers have stopped execution and are ready to
         * transfer tasks
         */
        private void transferReady() {
            synchronized (transferReadyLock) {
                notifyAll();
            }
        }

        /**
         * wait for the transfer to be done
         */
        public void awaitTransferDone() {
            ++schedulersReady;

            if (schedulersReady == 2)
                transferReady();

            synchronized (transferAwaitLock) {
                try {
                    wait();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * @return true, if this scheduler wants to transfer tasks
         */
        public boolean awaitingTransfer() {
            return awaitingTransfer;
        }

        /**
         * the transfer is done and both schedulers
         * are notified to continue their work
         */
        public void transferDone() {
            awaitingTransfer = false;
            schedulersReady = 0;

            synchronized (transferAwaitLock) {
                notifyAll();
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////
    // writelock
    ///////////////////////////////////////////////////////////////////////

    private class WriteLock {
        /**
         * the lock for all lockhelpers which are locked while
         * another lockhelper performs a writetask
         */
        private final Object  stopLock          = new Object();
        /**
         * the lock for the lockhelper waiting to perform a writetask
         */
        private final Object  awaitWriteRelease = new Object();
        /**
         * the number of lockhelpers which already have been locked
         */
        private       int     locked_count      = 0;
        /**
         * flag to check whether this lock is currently locked
         */
        private       boolean locked            = false;

        /**
         * requests a writelock
         * <p>
         * all lockhelpers except for the caller will
         * be locked by calling checkWriteLock
         * <p>
         * the callerthread will wait until all threads are locked
         */
        public void requestWriteLock() {
            locked_count = 0;

            locked = true;

            while (locked_count < maxThreadCount)
                synchronized (awaitWriteRelease) {
                    try {
                        awaitWriteRelease.wait();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }

        /**
         * releases the writelock
         * <p>
         * all lockhelpers which were locked when request
         * writelock was called will be released
         */
        public synchronized void releaseWriteLock() {
            //reset all vars
            locked = false;
            locked_count = 0;

            //notify the waiting lockhelpers
            //to continue their execution
            synchronized (stopLock) {
                stopLock.notifyAll();
            }
        }

        /**
         * if this writelock is locked the thread
         * calling this method will wait until the lock
         * is released by releaseWriteLock()
         */
        public synchronized void checkWriteLock() {
            if (!locked)
                return;

            locked_count++;

            //if all lockhelpers except for the one currently
            //requesting the lock are locked, notify the
            //requestor to perform his task
            if (locked_count == maxThreadCount - 1)
                synchronized (awaitWriteRelease) {
                    awaitWriteRelease.notifyAll();
                }

            //block this thread until the task was performed
            synchronized (stopLock) {
                try {
                    stopLock.wait();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}