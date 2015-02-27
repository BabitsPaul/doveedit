package dove.util.concurrent.access;

/**
 * Created by Babits on 10/02/2015.
 */
@FunctionalInterface
public interface AccessOp<R> {
    public R doOp()
            throws Exception;
}
