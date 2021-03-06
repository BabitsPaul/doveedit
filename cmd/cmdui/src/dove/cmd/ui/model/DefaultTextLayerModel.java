package dove.cmd.ui.model;

import java.util.ArrayList;
import java.util.List;

/**
 * NOTE: this model is always used with unclipped buffer
 */
public class DefaultTextLayerModel
        extends AbstractTextLayerModel {
    private ArrayList<String> lines;

    public DefaultTextLayerModel(CharBuffer buffer, Cursor cursor, ClipObject clip) {
        super(buffer, cursor, clip);

        lines = new ArrayList<>();
    }

    @Override
    public void removeChar() {
        //TODO doesn't work properly when after linebreak
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
        getCursor().moveCursorUp();
    }

    @Override
    public void cursorDown() {
        getCursor().moveCursorDown();
    }

    @Override
    public void cursorRight() {
        getCursor().moveCursorRight();
    }

    @Override
    public void cursorLeft() {
        getCursor().moveCursorLeft();
    }

    @Override
    public void nextLine() {
        fireLayerModelChanged(new CommandLineEvent(CommandLineEvent.PAINTING_DUMMY,
                CommandLineEvent.SOURCE_TYPE.PAINTING, CommandLineEvent.SUPPRESS_EVENT_RELATED_REPAINT));

        lines.add(searchLastLine());

        Cursor cursor = getCursor();
        CharBuffer buffer = getBuffer();
        ClipObject clip = getClip();

        //push the buffer up, if the cursor is in the last line
        if (!clip.inBoundsY(cursor.getY() + 1)) {
            buffer.pushContentUp(10);

            cursor.setY(getClip().convertToAbsoluteY(cursor.getY()) - 10);
        }

        //get first empty line, or push content up, if no empty lines are found
        int line = firstEmptyLineAfterCursor();
        if (line == -1) {
            buffer.pushContentUp(10);

            //first empty line is the first one that was freed by pushing the buffer
            line = buffer.getHeight() - 10;
        }

        char[][] buf = buffer.getContent();

        //create content of line the cursor is currently at
        //after the cursor
        char[] lineAfterCursor = new char[buffer.getWidth()];
        int j = 0;
        int i = cursor.getX();
        int cursorY = cursor.getY();
        for (; i < buffer.getWidth(); i++, j++) {
            lineAfterCursor[j] = buf[cursorY][i];
            buf[cursorY][i] = AbstractCommandLayer.NO_CHAR;
        }
        for (; j < buffer.getWidth(); j++)
            lineAfterCursor[j] = AbstractCommandLayer.NO_CHAR;

        //push lines down
        char[] currentLine;
        char[] replaceWith = lineAfterCursor;

        for (i = cursor.getY() + 1; i <= line; i++) {
            currentLine = buf[i];
            buf[i] = replaceWith;

            replaceWith = currentLine;
        }

        //set cursor to new position
        int y = cursor.getY() + 1;
        cursor.setX(0);
        cursor.setY(y);

        fireLayerModelChanged(new CommandLineEvent(CommandLineEvent.PAINTING_DUMMY,
                CommandLineEvent.SOURCE_TYPE.PAINTING, CommandLineEvent.ENABLE_EVENT_RELATED_REPAINT));

        fireLayerModelChanged(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.TEXT_LAYER_TYPE,
                TEXT_ADDED));
    }

    private int firstEmptyLineAfterCursor() {
        char[][] buffer = getBuffer().getContent();
        PositionHelper.Position cursorPos = getCursor().getPosition();

        int line = cursorPos.getY();
        for (; line < buffer.length; line++) {
            boolean allNoChar = true;

            for (char c : buffer[line]) {
                allNoChar = (c == AbstractCommandLayer.NO_CHAR);

                if (!allNoChar)
                    break;
            }

            if (allNoChar)
                return line;
        }

        return -1;
    }

    public String getLastLine() {
        return searchLastLine();
    }

    public List<String> listLines() {
        return lines;
    }

    public void write(String text) {
        for (char c : text.toCharArray())
            getBuffer().put(c);
    }

    public void writeln(String text) {
        write(text + "\n");
    }

    protected String searchLastLine() {
        StringBuilder builder = new StringBuilder("");

        CharBuffer buffer = getBuffer();

        PositionHelper.Position position = new PositionHelper.Position(getCursor().getX(), getCursor().getY(), false);
        PositionHelper helper = getHelper();

        char c;
        while (position.getX() != 0 && position.getY() != 0 && (c = buffer.get(position)) != '\n' && c != AbstractCommandLayer.NO_CHAR) {
            builder.append(c);

            position = helper.left(position);
        }

        builder.reverse();

        return builder.toString();
    }
}