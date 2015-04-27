
package dove.cmd.interpreter;

import dove.cmd.CommandLineConfiguration;
import dove.cmd.ui.model.*;
import dove.util.collections.FSRRStack;

import java.io.File;

public class CommandLineLayerModel
        extends AbstractTextLayerModel {
    private int cmdStartX;

    private int cmdStartY;

    private int cmdEndY;

    private int cmdEndX;

    private int currentCmd;

    private boolean blockInput;

    private FSRRStack<String> prevCmds;

    private String lastTyped;

    private CommandLineInterpreter interpreter;

    private CommandLineConfiguration cfg;

    public CommandLineLayerModel(CharBuffer buffer, Cursor cursor, ClipObject clip, CommandLineInterpreter interpreter) {
        super(buffer, cursor, clip);

        cfg = new CommandLineConfiguration();
        initCfg();

        cmdStartY = cursor.getY();

        prevCmds = new FSRRStack<>((Integer) cfg.get("cmd.memory.cmds"), String.class);

        this.interpreter = interpreter;
        initInterpreter();

        cmdStartX = 0;
        cmdStartY = 0;
        cmdEndX = 0;
        cmdEndY = 0;

        lastTyped = "";
        blockInput = false;
        currentCmd = -1;

        write(((File) interpreter.get("cmd.workingdirectory")).getAbsolutePath() + ">");
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

        currentCmd = -1;

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

        currentCmd = -1;

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

        fireLayerModelChanged(new CommandLineEvent(CommandLineEvent.PAINTING_DUMMY,
                CommandLineEvent.SOURCE_TYPE.PAINTING, CommandLineEvent.SUPPRESS_EVENT_RELATED_REPAINT));

        String nextCmd;
        if (currentCmd == -1) {
            lastTyped = currentCommand();

            currentCmd = prevCmds.size() - 1;
        }
        else
            --currentCmd
        nextCmd = (currentCmd == -1 ? lastTyped : prevCmds.get(currentCmd));

        //save position of the cursor
        PositionHelper.Position tmpPos = getCursor().getPosition();

        //write the new command
        getCursor().setPosition(new PositionHelper.Position(cmdStartX, cmdStartY, false));
        write(nextCmd);

        //place the cursor at either the end of the new command or tmpPos, if tmpPos is in bounds of the new command
        PositionHelper.Position cmdEnd = getCursor().getPosition();
        getCursor().setPosition(getHelper().after(tmpPos, cmdEnd) ? cmdEnd : tmpPos);

        fireLayerModelChanged(new CommandLineEvent(CommandLineEvent.PAINTING_DUMMY,
                CommandLineEvent.SOURCE_TYPE.PAINTING, CommandLineEvent.ENABLE_EVENT_RELATED_REPAINT));

        fireLayerModelChanged(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.TEXT_LAYER_TYPE, TEXT_UPDATED));
    }

    @Override
    public void cursorDown() {
        if (blockInput) return;

        fireLayerModelChanged(new CommandLineEvent(CommandLineEvent.PAINTING_DUMMY,
                CommandLineEvent.SOURCE_TYPE.PAINTING, CommandLineEvent.SUPPRESS_EVENT_RELATED_REPAINT));

        String nextCmd;
        if (currentCmd == -1)
            lastTyped = currentCommand();

        if (!prevCmds.isEmpty())
            currentCmd = 0;
        else {
            ++currentCmd;

            if (currentCmd == prevCmds.size())
                currentCmd = -1;
        }
        nextCmd = (currentCmd == -1 ? lastTyped : prevCmds.get(currentCmd));

        //save position of the cursor
        PositionHelper.Position tmpPos = getCursor().getPosition();

        //write the new command
        getCursor().setPosition(new PositionHelper.Position(cmdStartX, cmdStartY, false));
        write(nextCmd);

        //place the cursor at either the end of the new command or tmpPos, if tmpPos is in bounds of the new command
        PositionHelper.Position cmdEnd = getCursor().getPosition();
        getCursor().setPosition(getHelper().after(tmpPos, cmdEnd) ? cmdEnd : tmpPos);

        fireLayerModelChanged(new CommandLineEvent(CommandLineEvent.PAINTING_DUMMY,
                CommandLineEvent.SOURCE_TYPE.PAINTING, CommandLineEvent.ENABLE_EVENT_RELATED_REPAINT));

        fireLayerModelChanged(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.TEXT_LAYER_TYPE, TEXT_UPDATED));
    }

    @Override
    public void cursorRight() {
        if (blockInput) return;

        currentCmd = -1;

        PositionHelper.Position p = getCursor().getPosition();
        p = getHelper().right(p);

        if (inLineBounds(p))
            getCursor().setPosition(p);
    }

    @Override
    public void cursorLeft() {
        if (blockInput) return;

        currentCmd = -1;

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

        String dir = ((File) interpreter.get("cmd.workingdirectory")).getAbsolutePath();

        getCursor().moveCursorDown();
        getCursor().setX(0);

        write(dir + ">");

        blockInput = false;
    }

    public void write(String txt) {
        fireLayerModelChanged(new CommandLineEvent(CommandLineEvent.PAINTING_DUMMY,
                CommandLineEvent.SOURCE_TYPE.PAINTING, CommandLineEvent.SUPPRESS_EVENT_RELATED_REPAINT));

        PositionHelper.Position end = end(getCursor().getPosition(), txt);
        if (end.getY() > getBuffer().getHeight())
            getBuffer().pushContentUp(Math.max(end.getY() - getBuffer().getHeight(), 20));

        for (char c : txt.toCharArray())
            getBuffer().put(c);

        fireLayerModelChanged(new CommandLineEvent(CommandLineEvent.PAINTING_DUMMY,
                CommandLineEvent.SOURCE_TYPE.PAINTING, CommandLineEvent.ENABLE_EVENT_RELATED_REPAINT));

        fireLayerModelChanged(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.TEXT_LAYER_TYPE, TEXT_ADDED));
    }

    public void writeLine(String txt) {
        write(txt + "\n");
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

    private PositionHelper.Position end(PositionHelper.Position start, String txt) {
        int x;
        int y;

        int width = getBuffer().getWidth();

        if (txt.contains("\n")) {
            String[] tmp = txt.split("\n");

            int lines = (tmp[0].length() + start.getX()) / width;
            for (int i = 1; i < tmp.length; i++)
                lines += (tmp[i].length() / width);

            y = lines + start.getY();
            x = tmp[tmp.length - 1].length() % width;
        }
        else {
            //one line of text -> fit line into commandline
            if (txt.length() > width - start.getX()) {
                //text is longer than one line

                //length after the first line
                int tmp = txt.length() - width - start.getX();

                x = tmp % width;

                y = tmp / width + 1;
            }
            else {
                x = start.getX() + txt.length();
                y = start.getY();
            }
        }

        return new PositionHelper.Position(x, y, false);
    }

    private String currentCommand() {
        String result = "";

        CharBuffer buffer = getBuffer();

        PositionHelper.Position p = new PositionHelper.Position(cmdStartX, cmdStartY, false);

        PositionHelper.Position cmdEnd = new PositionHelper.Position(cmdEndX, cmdEndY, false);
        PositionHelper helper = getHelper();

        do {
            result += buffer.get(p);

            p = helper.right(p);
        }
        while (!helper.after(p, cmdEnd));

        return result;
    }
}