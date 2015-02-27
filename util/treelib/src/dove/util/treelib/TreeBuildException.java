package dove.util.treelib;

public class TreeBuildException
        extends Exception {
    public TreeBuildException(String msg, Exception cause) {
        super(msg, cause);
    }

    public TreeBuildException(String msg) {
        super(msg);
    }
}