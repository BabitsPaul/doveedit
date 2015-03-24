package dove.cmd.ui.model;

import java.util.ArrayList;
import java.util.List;

public class DefaultTextLayerModel
        extends AbstractTextLayerModel {
    private ArrayList<String> lines;

    public DefaultTextLayerModel(CharBuffer buffer, Cursor cursor, ClipObject clip) {
        super(buffer, cursor, clip);

        lines = new ArrayList<>();
    }

    @Override
    public void removeChar() {
        fireLayerModelChanged(new CommandLineEvent(CommandLineEvent.PAINTING_DUMMY,
                CommandLineEvent.SOURCE_TYPE.PAINTING, CommandLineEvent.SUPPRESS_EVENT_RELATED_REPAINT));

        Cursor cursor = getCursor();
        CharBuffer buffer = getBuffer();
        PositionHelper helper = getHelper();
        ClipObject clip = getClip();

        char c;
        int x = cursor.getX();
        int y = cursor.getY();
        PositionHelper.Position toReplace = new PositionHelper.Position(x, y, clip.isEnabled());
        PositionHelper.Position swapToLeft = helper.right(new PositionHelper.Position(x, y, clip.isEnabled()));
        do {
            c = buffer.get(swapToLeft);
            buffer.put(c, toReplace);

            toReplace = swapToLeft;
            swapToLeft = helper.right(swapToLeft);
        }
        while (c != AbstractCommandLayer.NO_CHAR);

        cursor.setPosition(helper.left(new PositionHelper.Position(x, y, clip.isEnabled())));

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
        PositionHelper.Position currentPos = new PositionHelper.Position(cursor.getX(), cursor.getY(), getClip().isEnabled());
        while (prev != AbstractCommandLayer.NO_CHAR) {
            swap = buffer.get(currentPos);
            buffer.put(prev, currentPos);

            currentPos = helper.right(currentPos);

            prev = swap;
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
        lines.add(searchLastLine());

        getBuffer().put('\n');
    }

    @Override
    public String getLastLine() {
        return searchLastLine();
    }

    @Override
    public List<String> listLines() {
        return lines;
    }

    @Override
    public void write(String text) {
        for (char c : text.toCharArray())
            getBuffer().put(c);
    }

    @Override
    public void writeln(String text) {
        write(text + "\n");
    }

    protected String searchLastLine() {
        StringBuilder builder = new StringBuilder("");

        CharBuffer buffer = getBuffer();

        int x = getCursor().getX();
        int y = getCursor().getY();

        char c;
        while (x != 0 && y != 0 && (c = buffer.get(x, y)) != '\n' && c != AbstractCommandLayer.NO_CHAR)
            builder.append(c);

        builder.reverse();

        return builder.toString();
    }
}