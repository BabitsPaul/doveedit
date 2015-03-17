package dove.cmd.ui.model;

import java.util.ArrayList;
import java.util.List;

public class DefaultTextLayerModel
        extends AbstractTextLayerModel {
    private ArrayList<String> lines;

    public DefaultTextLayerModel(InternalCharBuffer buffer, InternalCursor cursor) {
        super(buffer, cursor);
    }

    @Override
    public void removeChar() {
        InternalCursor cursor = getCursor();

        int x = cursor.getX();
        int y = cursor.getY();

        getCursor().moveCursorLeft();

        char c;
        do {
            getBuffer().put(c = getBuffer().get(x, y));
        }
        while (c != '\n');
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

        InternalCharBuffer buffer = getBuffer();

        int x = getCursor().getX();
        int y = getCursor().getY();

        char c;
        while (x != 0 && y != 0 && (c = buffer.get(x, y)) != '\n')
            builder.append(c);

        builder.reverse();

        return builder.toString();
    }
}