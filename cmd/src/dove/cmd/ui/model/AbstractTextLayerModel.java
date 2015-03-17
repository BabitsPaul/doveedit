package dove.cmd.ui.model;

import java.util.List;

public abstract class AbstractTextLayerModel
        extends AbstractLayerModel {
    private InternalCharBuffer buffer;

    private InternalCursor cursor;

    public AbstractTextLayerModel(InternalCharBuffer buffer, InternalCursor cursor) {
        this.buffer = buffer;
        this.cursor = cursor;
    }

    protected InternalCursor getCursor() {
        return cursor;
    }

    protected InternalCharBuffer getBuffer() {
        return buffer;
    }

    public abstract void removeChar();

    public abstract void addChar(char c);

    public abstract void cursorUp();

    public abstract void cursorDown();

    public abstract void cursorRight();

    public abstract void cursorLeft();

    public abstract void nextLine();

    public abstract String getLastLine();

    public abstract List<String> listLines();

    public abstract void write(String text);

    public abstract void writeln(String text);
}