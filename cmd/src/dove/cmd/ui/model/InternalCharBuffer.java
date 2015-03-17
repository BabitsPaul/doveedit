package dove.cmd.ui.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * represents the screenbuffer
 * <p>
 * this buffer is used to store the content that is
 * currently available for display on the screen
 */
public class InternalCharBuffer {
    /**
     * the
     */
    public static final int BUFFER_UPDATED = 0;

    public static final int BUFFER_CLIPPED = 1;

    private int width;

    private int height;

    private int clipOffsetX;

    private int clipOffsetY;

    private int clipWidth;

    private int clipHeight;

    private boolean clipped;

    private ArrayList<CommandLineUIListener> listeners;

    private InternalCursor cursor;

    private char[][] buffer;

    private Color[][] colors;

    private Color defaultColor;

    public InternalCharBuffer(int width, int height, InternalCursor cursor, Color defaultColor) {
        this.buffer = new char[height][width];
        this.colors = new Color[height][width];

        this.width = width;
        this.height = height;

        this.cursor = cursor;

        this.defaultColor = defaultColor;

        listeners = new ArrayList<>();

        for (Color[] colors : this.colors)
            Arrays.fill(colors, defaultColor);

        addCommandLineListener(cursor);
    }

    /////////////////////////////////////////////////////////////
    // size
    /////////////////////////////////////////////////////////////

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

    //////////////////////////////////////////////////////////
    // write/read
    //////////////////////////////////////////////////////////

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

        int offsetX = (clipped ? col - clipOffsetX : col);
        int offsetY = (clipped ? line - clipOffsetY : line);

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

    /**
     * returns the content of the buffer or
     * if clipping is active, the currently available part of the buffer
     * warning: content of the result of this
     * method shouldn't be manipulated, since it might,
     * or might not have impact on  the actual
     * buffer data (dependend upon whether clipping is active or not)
     *
     * @return an array containing the currently active
     */
    public char[][] getContent() {
        if (!clipped)
            return buffer;

        char[][] clippedArea = new char[clipHeight][clipWidth];

        for (int i = 0; i < clipWidth; i++)
            for (int j = 0; j < clipHeight; j++)
                clippedArea[j][i] = buffer[j + clipOffsetY][i + clipOffsetX];

        return clippedArea;
    }

    public void put(char c) {
        if (clipped) {
            int absoluteX = cursor.getX() + clipOffsetX;
            int absoluteY = cursor.getY() + clipOffsetY;

            buffer[absoluteY][absoluteX] = c;
        }
        else
            buffer[cursor.getY()][cursor.getX()] = c;

        cursor.moveCursorRight();

        fireCommandLineEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.BUFFER_TYPE, BUFFER_UPDATED));
    }

    public char get(int x, int y) {
        if (clipped) {
            int absoluteX = x - clipOffsetX;
            int absoluteY = y - clipOffsetY;

            if (absoluteX < 0 || absoluteX >= clipWidth ||
                    absoluteY < 0 || absoluteY >= clipHeight)
                throw new ArrayIndexOutOfBoundsException("size: (" + clipWidth + "/" + clipHeight +
                        ") requested: (" + x + "/" + y + ")");

            return buffer[absoluteY][absoluteX];
        }
        else
            return buffer[y][x];
    }

    /////////////////////////////////////////////////////////////
    // color
    /////////////////////////////////////////////////////////////

    public Color getColor(int x, int y) {
        if (clipped) {
            int absoluteX = x - clipOffsetX;
            int absoluteY = y - clipOffsetY;

            if (absoluteX < 0 || absoluteX >= clipWidth ||
                    absoluteY < 0 || absoluteY >= clipHeight)
                throw new ArrayIndexOutOfBoundsException("size: (" + clipWidth + "/" + clipHeight +
                        ") requested: (" + x + "/" + y + ")");

            return colors[x][y];
        }
        else
            return colors[x][y];
    }

    public Color[][] getColors() {
        if (clipped) {
            Color[][] colorTemp = new Color[clipWidth][clipHeight];
            for (int line = 0; line < clipHeight; line++)
                for (int col = 0; col < clipWidth; col++)
                    colorTemp[line][col] = colors[line + clipOffsetX][col + clipOffsetY];

            return null;
        }
        else
            return colors;
    }

    public void putColor(Color c) {
        colors[cursor.getY()][cursor.getX()] = c;
    }

    /////////////////////////////////////////////////////////////
    // moving content
    /////////////////////////////////////////////////////////////

    /**
     * moves the content of the buffer up for dy
     * all emptied lines will be filled with NO_CHAR and
     * the default color
     *
     * @param dy the number of lines to move the buffer up
     */
    public void pushContentUp(int dy) {
        if (dy < 0)
            throw new IllegalArgumentException("Invalid move-argument - must be positive");
        if (dy >= height)
            throw new ArrayIndexOutOfBoundsException("Invalid argument - must be < " + height);

        System.arraycopy(buffer, dy, buffer, 0, height - dy);
        System.arraycopy(colors, dy, colors, 0, height - dy);

        for (int i = height - dy; i < height; i++) {
            Arrays.fill(buffer[i], AbstractCommandLayer.NO_CHAR);
            Arrays.fill(colors[i], defaultColor);
        }
    }

    /////////////////////////////////////////////////////////////
    // clipping
    /////////////////////////////////////////////////////////////

    /**
     * sets the cliparea to the specified area
     * this will restrict access/writing to this area
     * and recalculates all coordinates for methods
     * absolute address in the buffer = clipOffsetX + accessaddress
     * (same for y)
     *
     * @param x      the xoffset of the clipped area
     * @param y      the yoffset of the clipped area
     * @param width  the width of the clipped area
     * @param height the height of the clipped area
     */
    public void clipBuffer(int x, int y, int width, int height) {
        clipped = true;

        clipOffsetX = x;
        clipOffsetY = y;
        clipWidth = width;
        clipHeight = height;

        fireCommandLineEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.BUFFER_TYPE, BUFFER_CLIPPED));
    }

    /**
     * disables the cliping
     * <p>
     * access to the buffer will be reset to
     * normal addressing (no offsets or restricted width)
     */
    public void reverseClip() {
        clipped = false;

        fireCommandLineEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.BUFFER_TYPE, BUFFER_CLIPPED));
    }

    public boolean isClipped() {
        return clipped;
    }

    ///////////////////////////////////////////////////////////////
    // commandlineevent
    ///////////////////////////////////////////////////////////////

    public void addCommandLineListener(CommandLineUIListener l) {
        listeners.add(l);
    }

    public void removeListener(CommandLineUIListener l) {
        listeners.remove(l);
    }

    protected void fireCommandLineEvent(CommandLineEvent e) {
        listeners.forEach(l -> l.commandLineChanged(e));
    }
}