package dove.cmd.runable;

/**
 * Created by Babits on 15/06/2015.
 */
public class ParserException
        extends Exception {
    private String line;
    private int startAt, endAt;

    public ParserException(String msg, String line, int startAt, int endAt) {
        super(msg);

        this.line = line;
        this.startAt = startAt;
        this.endAt = endAt;
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
