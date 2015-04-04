package dove.cmd.interpreter;

import dove.cmd.ui.model.*;
import dove.util.collections.FixedSizeRAStack;

public class CommandLineLayerModel
        extends AbstractTextLayerModel {
    private int cmdStartX;

    private int cmdStartY;

    private int cmdEndY;

    private int cmdEndX;

    private int currentCmd = 0;

    private boolean blockInput = false;

    private FixedSizeRAStack<String> prevCmds;

    private CommandLineInterpreter interpreter;

    public CommandLineLayerModel(CharBuffer buffer, Cursor cursor, ClipObject clip, CommandLineInterpreter interpreter) {
        super(buffer, cursor, clip);

        cmdStartY = cursor.getY();

        prevCmds = new FixedSizeRAStack<>(50, String.class);

        this.interpreter = interpreter;
    }

    @Override
    public void removeChar() {

    }

    @Override
    public void addChar(char c) {

    }

    @Override
    public void cursorUp() {

    }

    @Override
    public void cursorDown() {

    }

    @Override
    public void cursorRight() {

    }

    @Override
    public void cursorLeft() {

    }

    @Override
    public void nextLine() {
        interpreter.doCommand(currentCommand());
    }

    public void write(String txt) {

    }

    public void writeLine(String txt) {

    }

    private String prevCommand() {
        return null;
    }

    private String nextCommand() {
        return null;
    }

    private boolean inLineBounds(PositionHelper.Position position) {
        PositionHelper helper = getHelper();

        PositionHelper.Position start = new PositionHelper.Position(cmdStartX, cmdStartY, false);
        PositionHelper.Position end = new PositionHelper.Position(cmdEndX, cmdEndY, false);

        start = helper.left(start);
        end = helper.right(end);

        PositionHelper.Position cursor = new PositionHelper.Position(getCursor().getX(), getCursor().getY(), false);

        return (helper.after(cursor, start) && helper.before(cursor, end));
    }

    private String currentCommand() {
        String result = "";

        CharBuffer buffer = getBuffer();

        PositionHelper.Position p = new PositionHelper.Position(cmdStartX, cmdStartY, false);

        do {
            result += buffer.get(p);

            p = getHelper().right(p);
        }
        while (!(p.getX() == cmdEndX && p.getY() == cmdEndY));

        return result;
    }
}