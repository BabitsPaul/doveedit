package dove.util.concurrent.access;

/**
 * Created by Babits on 10/02/2015.
 */
public final class AccessOpHelper<R>
        implements Runnable {
    private R result;

    private AccessOp<R> op;

    private ExceptionWrapper wrapper;

    public AccessOpHelper(AccessOp<R> op, ExceptionWrapper wrapper) {
        this.op = op;

        this.wrapper = wrapper;
    }

    public void run() {
        wrapper.reset();

        try {
            result = op.doOp();
        }
        catch (Exception e) {
            wrapper.handle(e);
        }
    }

    public void checkException()
            throws Exception {
        wrapper.check();
    }

    public R getResult() {
        return result;
    }

    public AccessOp<R> getOp() {
        return op;
    }

    public R finish()
            throws Exception {
        checkException();

        return result;
    }
}