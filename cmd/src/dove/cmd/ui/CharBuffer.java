package dove.cmd.ui;

import java.util.ArrayList;

/**
 * represents the screenbuffer
 * <p>
 * this buffer is used to store the content that is
 * currently available for display on the screen
 */
public class CharBuffer {
    private static final int BUFFER_UPDATED = 0;

    private int width;

    private int height;

    private int clipX;

    private int clipY;

    private int clipWidth;

    private int clipHeight;

    private boolean clipped;

    private ArrayList<CommandLineListener> listeners;

    /**
     * array of lines
     */
    private char[][] buffer;

    public CharBuffer(int width, int height) {
        this.buffer = new char[height][width];

        this.width = width;
        this.height = height;

        listeners = new ArrayList<>();
    }

    public int getWidth() {
        if (clipped)
            return clipWidth;
        else
            return width;
    }

    public int getHeight() {
        if (clipped)
            return clipHeight;
        else
            return height;
    }

    /**
     * inserts data at the specified position (clipping!!!)
     *
     * @param data the data to insert
     * @param col  xposition
     * @param line yposition
     */
    public void setContent(char[][] data, int col, int line) {
        if (data.length == 0)
            return;

        int offsetX = (clipped ? col - clipX : col);
        int offsetY = (clipped ? line - clipY : line);

        int insertWidth = (data.length == 0 ? 0 : data[0].length);
        int insertHeight = data.length;

        if (offsetX < 0 || offsetX + insertWidth >= width
                || offsetY < 0 || offsetY + insertHeight >= height)
            throw new ArrayIndexOutOfBoundsException("Invalid insertposition");

        for (int x = 0; x < insertWidth; x++)
            for (int y = 0; y < insertHeight; y++)
                buffer[y + offsetY][x + offsetX] = data[y][x];

        fireCommandLineEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.BUFFER_TYPE, BUFFER_UPDATED));
    }


    public void clipBuffer(int x, int y, int width, int height) {
        clipped = true;

        clipX = x;
        clipY = y;
        clipWidth = width;
        clipHeight = height;
    }

    public void reverseClip() {
        clipped = false;
    }

    public void addListener(CommandLineListener l) {
        listeners.add(l);
    }

    public void removeListener(CommandLineListener l) {
        listeners.remove(l);
    }

    protected void fireCommandLineEvent(CommandLineEvent e) {
        listeners.forEach(l -> l.commandLineChanged(e));
    }
}
