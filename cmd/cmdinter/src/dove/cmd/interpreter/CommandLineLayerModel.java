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

    private int currentCmd;

    private boolean blockInput;

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

        cmdStartX = 0;
        cmdStartY = 0;
        cmdEndX = 0;
        cmdEndY = 0;

        lastCmd = "";
        blockInput = false;
        currentCmd = -1;
    }

    public void initCfg() {
        cfg.put("cmd.memory.cmds", 50);
    }

    public void initInterpreter() {
        interpreter.put("cmd.workingdirectory", new File("C:/"));
    }

    @Override
    public void removeChar() {
        if (blockInput || (cmdStartX == cmdEndX && cmdStartY == cmdEndY)) return;

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

        //update bounds of current command
        PositionHelper.Position cmdEnd = new PositionHelper.Position(cmdEndX, cmdEndY, false);
        cmdEnd = helper.left(cmdEnd);
        cmdEndX = cmdEnd.getX();
        cmdEndY = cmdEnd.getY();

        fireLayerModelChanged(new CommandLineEvent(CommandLineEvent.PAINTING_DUMMY,
                CommandLineEvent.SOURCE_TYPE.PAINTING, CommandLineEvent.ENABLE_EVENT_RELATED_REPAINT));

        fireLayerModelChanged(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.TEXT_LAYER_TYPE, TEXT_REMOVED));
    }

    @Override
    public void addChar(char c) {
        if (blockInput) return;

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

        PositionHelper.Position cmdEnd = new PositionHelper.Position(cmdEndX, cmdEndY, false);
        cmdEnd = helper.right(cmdEnd);
        cmdEndX = cmdEnd.getX();
        cmdEndY = cmdEnd.getY();

        fireLayerModelChanged(new CommandLineEvent(CommandLineEvent.PAINTING_DUMMY,
                CommandLineEvent.SOURCE_TYPE.PAINTING, CommandLineEvent.ENABLE_EVENT_RELATED_REPAINT));

        fireLayerModelChanged(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.TEXT_LAYER_TYPE, TEXT_ADDED));
    }

    @Override
    public void cursorUp() {
        if (blockInput) return;
    }

    @Override
    public void cursorDown() {
        if (blockInput) return;
    }

    @Override
    public void cursorRight() {
        if (blockInput) return;

        PositionHelper.Position p = getCursor().getPosition();
        p = getHelper().right(p);

        if (inLineBounds(p))
            getCursor().setPosition(p);
    }

    @Override
    public void cursorLeft() {
        if (blockInput) return;

        PositionHelper.Position p = getCursor().getPosition();
        p = getHelper().right(p);

        if (inLineBounds(p))
            getCursor().setPosition(p);
    }

    @Override
    public void nextLine() {
        if (blockInput) return;

        blockInput = true;

        interpreter.doCommand(currentCommand());

        CharBuffer buffer = getBuffer();
        String dir = (String) interpreter.get("cmd.workingdirectory");

        for (int i = 0; i < dir.length(); i++)
            buffer.put(dir.charAt(i));

        blockInput = false;
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