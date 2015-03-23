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

        char c;
        int x = cursor.getX();
        int y = cursor.getY();
        do {
            c = buffer.get(x, y);
            buffer.put(c, x - 1, y);
        }
        while (c != AbstractCommandLayer.NO_CHAR);

        fireLayerModelChanged(new CommandLineEvent(CommandLineEvent.PAINTING_DUMMY,
                CommandLineEvent.SOURCE_TYPE.PAINTING, CommandLineEvent.ENABLE_EVENT_RELATED_REPAINT));

        fireLayerModelChanged(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.TEXT_LAYER_TYPE, TEXT_REMOVED));
    }

    @Override
    public void addChar(char c) {
        if (c == '\n' || c == '\r' || c == '\b')
            return;

        getBuffer().put(c);
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