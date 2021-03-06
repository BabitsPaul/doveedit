package dove.cmd.ui.model;

/**
 * models the commandlinecursor
 */
public class Cursor
        extends CommandLineElement {
    /**
     * eventconstant for moving the cursor
     */
    public static final int CURSOR_MOVED = 0;

    /**
     * eventconstant for changing visibility of the cursor
     */
    public static final int CURSOR_VISIBILITY_CHANGED = 1;

    /**
     * the x-position of the cursor
     */
    private int x;

    /**
     * the y-position of the cursor
     */
    private int y;

    /**
     * true, if this cursor should be showed in the model
     */
    private boolean isVisible;

    private ClipObject clip;

    private PositionHelper helper;

    /**
     * creates a new commandlinecursor with position 0,0
     * and the specified clip
     *
     * @param clip the Clip for the buffer this cursor moves on
     */
    public Cursor(ClipObject clip) {
        this.clip = clip;

        helper = new PositionHelper(clip);

        x = 0;
        y = 0;
    }

    /////////////////////////////////////////////////////
    // cursor position
    /////////////////////////////////////////////////////

    /**
     * returns the current x-position of the cursor
     *
     * @return the xposition
     */
    public int getX() {
        return clip.convertToRelativeX(x);
    }

    /**
     * sets the cursorposition to the new xposition
     *
     * @param nx new cursorposition
     */
    public void setX(int nx) {
        x = clip.convertToAbsoluteX(nx);

        fireCommandLineEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.CURSOR_TYPE, CURSOR_MOVED));
    }

    /**
     * returns the current y-position of the cursor
     *
     * @return the yposition
     */
    public int getY() {
        return clip.convertToRelativeY(y);
    }

    /**
     * sets the cursorposition to the new yposition
     *
     * @param ny new cursorposition
     */
    public void setY(int ny) {
        y = clip.convertToAbsoluteY(ny);

        fireCommandLineEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.CURSOR_TYPE, CURSOR_MOVED));
    }

    /**
     * moves the cursor one cell to the left, or one up and
     * to the end of the previous line, if the cursor is at
     * beginning of the current line
     * <p>
     * the cursor will not be moved any further, if it is positioned
     * at x == 0 and y == 0
     */
    public void moveCursorLeft() {
        PositionHelper.Position position = new PositionHelper.Position(x, y, false);

        position = helper.left(position);

        x = position.getX();
        y = position.getY();

        fireCommandLineEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.CURSOR_TYPE, CURSOR_MOVED));
    }

    /////////////////////////////////////////////////////
    // move cursor
    /////////////////////////////////////////////////////

    public void moveCursorRight() {
        PositionHelper.Position position = new PositionHelper.Position(x, y, false);

        position = helper.right(position);

        x = position.getX();
        y = position.getY();

        fireCommandLineEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.CURSOR_TYPE, CURSOR_MOVED));
    }

    public void moveCursorUp() {
        PositionHelper.Position position = helper.up(new PositionHelper.Position(x, y, false));

        x = position.getX();
        y = position.getY();

        fireCommandLineEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.CURSOR_TYPE, CURSOR_MOVED));
    }

    public void moveCursorDown() {
        PositionHelper.Position position = helper.down(new PositionHelper.Position(x, y, false));

        x = position.getX();
        y = position.getY();

        fireCommandLineEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.CURSOR_TYPE, CURSOR_MOVED));
    }

    public boolean isVisible() {
        return isVisible;
    }

    /////////////////////////////////////////////////////
    // visibility
    /////////////////////////////////////////////////////

    public void setVisible(boolean visible) {
        isVisible = visible;

        fireCommandLineEvent(new CommandLineEvent(this,
                CommandLineEvent.SOURCE_TYPE.CURSOR_TYPE, CURSOR_VISIBILITY_CHANGED));
    }

    public PositionHelper.Position getPosition() {
        return new PositionHelper.Position(x, y, clip.isEnabled());
    }

    public void setPosition(PositionHelper.Position position) {
        if (position.isRelative())
            position = helper.toAbsolute(position);

        x = position.getX();
        y = position.getY();

        fireCommandLineEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.CURSOR_TYPE, CURSOR_MOVED));
    }
}