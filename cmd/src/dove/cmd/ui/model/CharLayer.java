package dove.cmd.ui.model;

import java.awt.event.KeyListener;

/**
 * provides an interface for ascii-art / consolegames
 * if this charlayer is active, the buffer will be cliped to
 * restrict access to the part of the buffer, which is visible,
 * keyevents will be redirected to a listener
 * <p>
 * this layer cannot move the viewport
 */
public class CharLayer
        extends AbstractCommandLayer {
    public static final int LAYER_EDITED = 0;

    public static final int LAYER_ENABLED = 1;

    /**
     * the buffer with all displayed characters
     * <p>
     * the inner arrays represent the lines
     */
    private CharBuffer buffer;

    /**
     * the xoffset of this layer in the buffer
     */
    private int xOffSet;

    /**
     * the yoffsett of this layer in the buffer
     */
    private int yOffSet;

    /**
     * the widht of the layer in chars
     */
    private int width;

    /**
     * the height of the layer in chars
     */
    private int height;

    /**
     * the cursor of the commandlineui related to this layer
     */
    private CommandLineCursor cursor;

    /**
     * creates a new charlayer with the specified width and height
     *
     * @param listener
     * @param buffer
     * @param cursor
     * @param xOffSet
     * @param yOffSet
     * @param width
     * @param height
     */
    public CharLayer(KeyListener listener, CharBuffer buffer, CommandLineCursor cursor, int xOffSet, int yOffSet
            , int width, int height) {
        super(listener, cursor, buffer);

        this.width = width;
        this.height = height;
        this.buffer = buffer;
        this.xOffSet = xOffSet;
        this.yOffSet = yOffSet;

        this.buffer = buffer;
        this.cursor = cursor;
    }

    /**
     * cleares the specified position
     * this method takes care of the linewrapper
     *
     * @param x inline position in the buffer
     * @param y line onscreen
     */
    public void clear(int x, int y) {
        cursor.setX(x);
        cursor.setY(y);
        buffer.put(NO_CHAR);

        fireCommandLayerEvent(new CommandLineEvent(this, CommandLineEvent.SOURCE_TYPE.CHAR_LAYER_TYPE, LAYER_EDITED));
    }

    /**
     * puts a character at the cursorposition
     * and moves the cursor one to the left, or
     * one line down, if the line is full
     *
     * @param c the character to insert
     */
    public void put(char c) {
        buffer.put(c);
    }

    @Override
    public void enableLayer() {
        cursor.setVisible(false);

        buffer.clipBuffer(xOffSet, yOffSet, width, height);
    }

    /////////////////////////////////////////////////////////
    // clipping
    /////////////////////////////////////////////////////////

    public int getxOffSet() {
        return xOffSet;
    }

    public int getyOffSet() {
        return yOffSet;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}