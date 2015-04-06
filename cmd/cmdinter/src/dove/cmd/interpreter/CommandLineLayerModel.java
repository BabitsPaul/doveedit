package dove.cmd.interpreter;

import dove.cmd.CommandLineConfiguration;
import dove.cmd.ui.model.*;
import dove.util.collections.FixedSizeRAStack;

import java.io.File;

//TODO store currentLine as last Command, if going to older command
public class CommandLineLayerModel
        extends AbstractTextLayerModel {
    private int cmdStartX;

    private int cmdStartY;

    private int cmdEndY;

    private int cmdEndX;

    private int currentCmd = 0;

    private boolean blockInput = false;

    private FixedSizeRAStack<String> prevCmds;

    private String lastCmd;

    private CommandLineInterpreter interpreter;

    private CommandLineConfiguration cfg;

    public CommandLineLayerModel(CharBuffer buffer, Cursor cursor, ClipObject clip, CommandLineInterpreter interpreter) {
        super(buffer, cursor, clip);

        cfg = new CommandLineConfiguration();
        initCfg();

        cmdStartY = cursor.getY();

        prevCmds = new FixedSizeRAStack<>((Integer) cfg.get("cmd.memory.cmds"), String.class);

        this.interpreter = interpreter;
        initInterpreter();
    }

    public void initCfg() {
        cfg.put("cmd.memory.cmds", 50);
    }

    public void initInterpreter() {
        interpreter.put("cmd.workingdirectory", new File("C:/"));
    }

    @Override
    public void removeChar() {
        fireLayerModelChanged(new CommandLineEvent(CommandLineEvent.PAINTING_DUMMY,
                CommandLineEvent.SOURCE_TYPE.PAINTING, CommandLineEvent.SUPPRESS_EVENT_RELATED_REPAINT));

        Cursor cursor = getCursor();
        CharBuffer buffer = getBuffer();
        PositionHelper helper = getHelper();

        char c;
        int x = cursor.getX();
        int y = cursor.getY();
        PositionHelper.Position toReplace = new PositionHelper.Position(x, y, false);
        PositionHelper.Position swapToLeft = helper.right(new PositionHelper.Position(x, y, false));
        do {
            c = buffer.get(swapToLeft);
            buffer.put(c, toReplace);

            toReplace = swapToLeft;
            swapToLeft = helper.right(swapToLeft);
        }
        while (c != AbstractCommandLayer.NO_CHAR);

        cursor.setPosition(helper.left(new PositionHelper.Position(x, y, false)));

        fireLayerModelChanged(new CommandLineEvent(CommandLineEvent.PAINTING_DUMMY,
                CommandLineEvent.SOURCE_TYPE.PAINTING, CommandLineEvent.ENABLE_EVENT_RELATED_REPAINT));

        fireLayerModelChanged(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.TEXT_LAYER_TYPE, TEXT_REMOVED));
    }

    @Override
    public void addChar(char c) {
        if (c == '\n' || c == '\r' || c == '\b')
            return;

        fireLayerModelChanged(new CommandLineEvent(CommandLineEvent.PAINTING_DUMMY,
                CommandLineEvent.SOURCE_TYPE.PAINTING, CommandLineEvent.SUPPRESS_EVENT_RELATED_REPAINT));

        Cursor cursor = getCursor();
        CharBuffer buffer = getBuffer();
        PositionHelper helper = getHelper();

        PositionHelper.Position cursorPosition = cursor.getPosition();

        char prev = c;
        char swap;
        PositionHelper.Position currentPos = new PositionHelper.Position(cursor.getX(), cursor.getY(), false);
        while (prev != AbstractCommandLayer.NO_CHAR) {
            swap = buffer.get(currentPos);
            buffer.put(prev, currentPos);

            currentPos = helper.right(currentPos);

            prev = swap;

            if (currentPos.getY() == buffer.getHeight() - 1 && currentPos.getX() == buffer.getWidth() - 1)
                buffer.pushContentUp(10);
        }

        cursor.setPosition(helper.right(cursorPosition));

        fireLayerModelChanged(new CommandLineEvent(CommandLineEvent.PAINTING_DUMMY,
                CommandLineEvent.SOURCE_TYPE.PAINTING, CommandLineEvent.ENABLE_EVENT_RELATED_REPAINT));

        fireLayerModelChanged(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.TEXT_LAYER_TYPE, TEXT_ADDED));
    }

    @Override
    public void cursorUp() {

    }

    @Override
    public void cursorDown() {

    }

    @Override
    public void cursorRight() {
        PositionHelper.Position p = getCursor().getPosition();
        p = getHelper().right(p);

        if (inLineBounds(p))
            getCursor().setPosition(p);
    }

    @Override
    public void cursorLeft() {
        PositionHelper.Position p = getCursor().getPosition();
        p = getHelper().right(p);

        if (inLineBounds(p))
            getCursor().setPosition(p);
    }

    @Override
    public void nextLine() {
        interpreter.doCommand(currentCommand());

        CharBuffer buffer = getBuffer();
        String dir = (String) interpreter.get("cmd.workingdirectory");

        for (int i = 0; i < dir.length(); i++)
            buffer.put(dir.charAt(i));
    }

    public void write(String txt) {

    }

    public void writeLine(String txt) {

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