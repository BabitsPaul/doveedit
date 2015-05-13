package dove.cmd.model;

/**
 * Created by Babits on 11/05/2015.
 */
public class InterpreterException
        extends Exception {
    private String atLine;
    private int atChar;

    public InterpreterException(String message, String atLine, int atChar) {
        super(message);
    }

    public String getLine() {
        return atLine;
    }

    public int atChar() {
        return atChar;
    }
}
