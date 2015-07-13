package dove.cmd.runable;

/**
 * Created by Babits on 15/06/2015.
 */
public class ParserException
        extends Exception {
    public static final int NOT_SPECIFIED = 0;
    public static final int BRACKET_MISSING = 1;
    public static final int UNKNOWN_EXPRESSION = 2;
    public static final int INVALID_INDENTION = 3;
    public static final int NOT_DEFINED = 4;
    public static final int INVALID_PARAMETER_TYPE = 5;
    public static final int INVALID_PARAMETER_COUNT = 6;
    public static final int INVALID_TOKEN = 7;
    public static final int INTERNAL_ERROR = 8;


    private String line;
    private int startAt, endAt;
    private int type;

    public ParserException(int type, String msg, String line, int startAt, int endAt) {
        super(msg);

        this.type = type;
        this.line = line;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public int getType() {
        return type;
    }

    public String getLine() {
        return line;
    }

    public int getStartAt() {
        return startAt;
    }

    public int getEndAt() {
        return endAt;
    }
}
