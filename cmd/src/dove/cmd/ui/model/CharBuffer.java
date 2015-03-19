package dove.cmd.ui.model;

import java.awt.*;
import java.util.Arrays;

/**
 * represents the screenbuffer
 * <p>
 * this buffer is used to store the content that is
 * currently available for display on the screen
 */
public class CharBuffer
        extends CommandLineElement {
    public static final int BUFFER_UPDATED = 0;

    private int width;

    private int height;

    private ClipObject clip;

    private Cursor cursor;

    private char[][] buffer;

    private Color[][] colors;

    private Color defaultColor;

    public CharBuffer(int width, int height, Cursor cursor, Color defaultColor, ClipObject clip) {
        this.buffer = new char[height][width];
        this.colors = new Color[height][width];

        this.width = width;
        this.height = height;

        this.cursor = cursor;

        this.defaultColor = defaultColor;

        this.clip = clip;

        for (Color[] colors : this.colors)
            Arrays.fill(colors, defaultColor);
    }

    /////////////////////////////////////////////////////////////
    // size
    /////////////////////////////////////////////////////////////

    public int getWidth() {
        return clip.getWidth();
    }

    public int getHeight() {
        return clip.getHeight();
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

        int offsetX = clip.convertToAbsoluteX(col);
        int offsetY = clip.convertToAbsoluteY(line);

        int insertWidth = (data.length == 0 ? 0 : data[0].length);
        int insertHeight = data.length;

        if (offsetX < 0 || offsetX + insertWidth >= getWidth()
                || offsetY < 0 || offsetY + insertHeight >= getHeight())
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
        if (clip.isEnabled())
            return buffer;

        int clipWidth = clip.getClipWidth();
        int clipHeight = clip.getClipHeight();

        char[][] clippedArea = new char[clipHeight][clipWidth];

        for (int i = 0; i < clipWidth; i++)
            for (int j = 0; j < clipHeight; j++)
                clippedArea[j][i] = buffer[j + clip.getOffSetY()][i + clip.getOffSetX()];

        return clippedArea;
    }

    public void put(char c) {
        int x = clip.convertToAbsoluteX(cursor.getX());
        int y = clip.convertToAbsoluteY(cursor.getY());

        buffer[y][x] = c;

        cursor.moveCursorRight();

        fireCommandLineEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.BUFFER_TYPE, BUFFER_UPDATED));
    }

    public char get(int x, int y) {
        x = clip.convertToAbsoluteX(x);
        y = clip.convertToAbsoluteY(y);

        if (x < 0 || x >= getWidth() ||
                y < 0 || y >= getHeight())
            throw new ArrayIndexOutOfBoundsException("size: (" + getWidth() + "/" + getHeight() +
                    ") requested: (" + x + "/" + y + ")");

        return buffer[y][x];
    }

    /////////////////////////////////////////////////////////////
    // color
    /////////////////////////////////////////////////////////////

    public Color getColor(int x, int y) {
        x = clip.convertToAbsoluteX(x);
        y = clip.convertToAbsoluteY(y);

        if (x < 0 || x >= getWidth() ||
                y < 0 || y >= getHeight())
            throw new ArrayIndexOutOfBoundsException("size: (" + getWidth() + "/" + getHeight() +
                    ") requested: (" + x + "/" + y + ")");

        return colors[x][y];
    }

    public Color[][] getColors() {
        if (clip.isEnabled()) {
            Color[][] colorTemp = new Color[clip.getClipHeight()][clip.getClipWidth()];
            for (int line = 0; line < clip.getClipHeight(); line++)
                for (int col = 0; col < clip.getClipWidth(); col++)
                    colorTemp[line][col] = colors[clip.convertToAbsoluteY(line)][clip.convertToAbsoluteX(col)];

            return colorTemp;
        }
        else
            return colors;
    }

    public void putColor(Color c) {
        colors[clip.convertToAbsoluteY(cursor.getY())][clip.convertToAbsoluteX(cursor.getX())] = c;
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
}