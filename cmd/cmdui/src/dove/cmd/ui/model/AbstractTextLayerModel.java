package dove.cmd.ui.model;

public abstract class AbstractTextLayerModel
        extends AbstractLayerModel {
    public static final int TEXT_ADDED   = 0;
    public static final int TEXT_REMOVED = 1;
    public static final int TEXT_UPDATED = 2;

    private CharBuffer buffer;

    private Cursor cursor;

    private PositionHelper helper;

    private ClipObject clip;

    public AbstractTextLayerModel(CharBuffer buffer, Cursor cursor, ClipObject clip) {
        this.buffer = buffer;
        this.cursor = cursor;

        this.clip = clip;

        this.helper = new PositionHelper(clip);
    }

    protected Cursor getCursor() {
        return cursor;
    }

    protected CharBuffer getBuffer() {
        return buffer;
    }

    protected PositionHelper getHelper() {
        return helper;
    }

    protected ClipObject getClip() {
        return clip;
    }

    public abstract void removeChar();

    public abstract void addChar(char c);

    public abstract void cursorUp();

    public abstract void cursorDown();

    public abstract void cursorRight();

    public abstract void cursorLeft();

    public abstract void nextLine();
}