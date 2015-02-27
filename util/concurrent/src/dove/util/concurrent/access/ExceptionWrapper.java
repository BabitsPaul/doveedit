package dove.util.concurrent.access;

/**
 * handles exceptions thrown by accessop
 */
public abstract class ExceptionWrapper {
    /**
     * the types of expected exceptions
     */
    private Class<? extends Exception>[] exceptions;
    /**
     * the caught exception
     */
    private Exception                    caughtException;
    /**
     * the type of the caught exception (no exception caught, expected exception, unexpected exception)
     */
    private ExceptionExpected unexpected       = ExceptionExpected.NO_EXCEPTION;
    /**
     * true, if the caught exception is a runtimeexception
     */
    private boolean           runtimeException = false;
    /**
     * true, if an exception was caught
     */
    private boolean           exceptionOccured = false;

    /**
     * creates a new exceptionwrapper with the given exceptions to catch
     *
     * @param exceptions list of expected exceptions
     */
    public ExceptionWrapper(Class<? extends Exception>[] exceptions) {
        this.exceptions = exceptions;
    }

    /**
     * handles the specified exception
     * and sets all correlated flags
     *
     * @param e caught exception
     */
    public void handle(Exception e) {
        caughtException = e;

        runtimeException = (e instanceof RuntimeException);

        for (Class<? extends Exception> c : exceptions) {
            if (c.isInstance(e))
                unexpected = ExceptionExpected.EXPECTED_EXCEPTION;
        }

        if (unexpected != ExceptionExpected.EXPECTED_EXCEPTION)
            unexpected = ExceptionExpected.UNEXPECTED_EXCEPTION;

        exceptionOccured = true;
    }

    /**
     * checks the type of the caught exception
     * and either throws it, if it's an unexpected runtimeexception
     * or handles it either as an expected or unexpected exception
     */
    public void check()
            throws Exception {
        if (unexpected == ExceptionExpected.NO_EXCEPTION)
            return;

        if (runtimeException && unexpected == ExceptionExpected.UNEXPECTED_EXCEPTION)
            throw caughtException;

        if (unexpected == ExceptionExpected.UNEXPECTED_EXCEPTION)
            caughtException = handleUnexpectedException();
        else
            caughtException = handleExpectedException();

        throwException();
    }

    /**
     * resets the wrapper
     */
    public void reset() {
        caughtException = null;
        unexpected = ExceptionExpected.NO_EXCEPTION;
        exceptions = null;
        exceptionOccured = true;
    }

    /**
     * @return the caught exception
     */
    public Exception getException() {
        return caughtException;
    }

    /**
     * handles expected exceptions
     */
    protected abstract Exception handleExpectedException();

    /**
     * handles unexpected exceptions
     */
    protected abstract Exception handleUnexpectedException();

    /**
     * @return true, if an exception occured while this wrapper was active
     */
    public boolean exceptionOccured() {
        return exceptionOccured;
    }

    /**
     * rethrow the exception
     */
    public <E extends Exception> void throwException()
            throws E {
        if (caughtException == null)
            return;

        throw (E) caughtException;
    }

    /**
     * flagtype for the caught exception
     */
    private enum ExceptionExpected {
        /**
         * no exception was thrown at all
         */
        NO_EXCEPTION,
        /**
         * the exception was not specified in exceptions
         */
        UNEXPECTED_EXCEPTION,
        /**
         * the exception was specified in exceptions
         */
        EXPECTED_EXCEPTION
    }
}