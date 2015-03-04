package dove.cmd.ui;

import java.util.ArrayList;

/**
 * models the commandlinecursor
 */
public class CommandLineCursor {
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
     * the screenwidth of the screen on which this cursor is placed
     */
    private int screenWidth;

    /**
     * the height of the screen this cursor is placed upon
     */
    private int screenHeight;

    /**
     * a list of listeners of this cursor
     */
    private ArrayList<CommandLineListener> listeners;

    /**
     * true, if this cursor should be showed in the ui
     */
    private boolean isVisible;

    /**
     * creates a new commandlinecursor with position 0,0
     * and the specified buffersize
     *
     * @param screenWidth  the bufferWidth
     * @param screenHeight the bufferHeight
     */
    public CommandLineCursor(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        x = 0;
        y = 0;

        listeners = new ArrayList<>();
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
        return x;
    }

    /**
     * sets the cursorposition to the new xposition
     *
     * @param nx new cursorposition
     */
    public void setX(int nx) {
        x = nx;

        fireCommandLineEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.CURSOR_TYPE, CURSOR_MOVED));
    }

    /**
     * returns the current y-position of the cursor
     *
     * @return the yposition
     */
    public int getY() {
        return y;
    }

    /**
     * sets the cursorposition to the new yposition
     *
     * @param ny new cursorposition
     */
    public void setY(int ny) {
        y = ny;

        fireCommandLineEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.CURSOR_TYPE, CURSOR_MOVED));
    }

    /////////////////////////////////////////////////////
    // move cursor
    /////////////////////////////////////////////////////

    /**
     * moves the cursor one cell to the left, or one up and
     * to the end of the previous line, if the cursor is at
     * beginning of the current line
     * <p>
     * the cursor will not be moved any further, if it is positioned
     * at x == 0 and y == 0
     */
    public void moveCursorLeft() {
        if (x == 0 && y == 0)
            return;

        x -= 1;

        if (x == -1) {
            x = screenWidth - 1;
            y -= 1;
        }

        fireCommandLineEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.CURSOR_TYPE, CURSOR_MOVED));
    }

    public void moveCursorRight() {
        if (x == screenWidth - 1 && y == screenHeight - 1)
            return;

        x = (x + 1) % screenWidth;

        if (x == 0)
            y += 1;

        fireCommandLineEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.CURSOR_TYPE, CURSOR_MOVED));
    }

    public void moveCursorUp() {
        if (y == 0)
            return;

        y -= 1;

        fireCommandLineEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.CURSOR_TYPE, CURSOR_MOVED));
    }

    public void moveCursorDown() {
        if (y == screenHeight - 1)
            return;

        y += 1;

        fireCommandLineEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.CURSOR_TYPE, CURSOR_MOVED));
    }

    /////////////////////////////////////////////////////
    // visibility
    /////////////////////////////////////////////////////

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;

        fireCommandLineEvent(new CommandLineEvent(this,
                CommandLineEvent.SOURCE_TYPE.CURSOR_TYPE, CURSOR_VISIBILITY_CHANGED));
    }

    /////////////////////////////////////////////////////
    // event listening
    /////////////////////////////////////////////////////

    public void addCommandLineListener(CommandLineListener l) {
        listeners.add(l);
    }

    public void removeCommandLineListener(CommandLineListener l) {
        listeners.remove(l);
    }

    protected void fireCommandLineEvent(CommandLineEvent e) {
        listeners.forEach(l -> l.commandLineChanged(e));
    }
}
