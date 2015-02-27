package dove.cmd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

import static java.awt.event.KeyEvent.*;

public class CommandLineUI
        extends JPanel
        implements KeyListener {
    /**
     * defines an empty char, which shouldnt be displayed
     */
    private static final char NO_CHAR      = (char) -1;
    /**
     * the console font
     */
    private static final Font COMMAND_FONT = new Font("Consolas", Font.PLAIN, 12);
    /**
     * the charlayer correlated to this commandline
     */
    private CharLayer charLayer;
    /**
     * the textlayer correlated to this commandline
     */
    private TextLayer textLayer;
    /**
     * the current state of this commandlineui
     */
    private COMMAND_LINE_MODE mode = COMMAND_LINE_MODE.LINE_MODE;
    /**
     * the commandlinemodel correlated to this ui
     */
    private CommandLineModel model;

    //////////////////////////////////////////////////////////////////
    // mode management
    //////////////////////////////////////////////////////////////////

    /**
     * creates a new commandlineui
     */
    public CommandLineUI() {
        super();

        model = new CommandLineModel();

        charLayer = new CharLayer(200, 100);
        textLayer = new TextLayer();

        addKeyListener(this);
    }

    /**
     * switches the mode of the commandline
     *
     * @param mode the new mode of the commandline
     */
    public void setCommandLineMode(COMMAND_LINE_MODE mode) {
        this.mode = mode;
    }

    /**
     * checks whether an operation is valid in the current mode
     *
     * @param check the commandlinemode to check
     * @return true, if the given mode is currently activated
     */
    private boolean checkMode(COMMAND_LINE_MODE check) {
        return (check.ordinal() == mode.ordinal());
    }

    ///////////////////////////////////////////////////////////////////
    // model
    ///////////////////////////////////////////////////////////////////

    /**
     * @return the commandlinemodel of this ui
     */
    public CommandLineModel getModel() {
        return model;
    }

    /**
     * paints this ui
     *
     * @param g the graphics to paint this component
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Color background = (Color) model.get("commandline.color.background");
        Color foreground = (Color) model.get("commandline.color.foreground");

        int lineheight = g.getFontMetrics(COMMAND_FONT).getHeight() + 2;

        g.setColor(background);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(foreground);
        g.setFont(COMMAND_FONT);

        for (int i = 0; i < charLayer.height; i++) {
            char[] line = charLayer.screenBuffer[(i + charLayer.lineWrapper) % charLayer.height];

            g.drawChars(line, 0, line.length, 5, (i + 1) * lineheight);
        }
    }

    ///////////////////////////////////////////////////////////////////
    // painting
    ///////////////////////////////////////////////////////////////////

    /**
     * moves the cursor to the given position
     *
     * @param x the position inline
     * @param y the line the cursor should be
     */
    public void moveCursor(int x, int y) {
        if (checkMode(COMMAND_LINE_MODE.LINE_MODE))
            return;

        y = (y + charLayer.lineWrapper) % charLayer.height;

        charLayer.moveCursor(x, y);
    }

    /////////////////////////////////////////////////////////////////////////////
    // public interface (grants public access to both text- and charlayer
    // and checks the mode of the commandline
    /////////////////////////////////////////////////////////////////////////////

    /**
     * puts a character at the current cursorposition
     *
     * @param c the character to insert
     */
    public void putChar(char c) {
        if (checkMode(COMMAND_LINE_MODE.LINE_MODE))
            return;

        charLayer.put(c);
    }

    /**
     * clears the given position
     *
     * @param x inline position
     * @param y line
     */
    public void clear(int x, int y) {
        if (checkMode(COMMAND_LINE_MODE.LINE_MODE))
            return;

        charLayer.clear(x, y);
    }

    /**
     * clears the complete buffer
     */
    public void clearAll() {
        charLayer.clearAll();
    }

    /**
     * puts the specified character at the cursor
     * in LINE_MODE
     *
     * @param c the character to insert
     * @see dove.cmd.CommandLineUI#putChar
     */
    public void write(char c) {
        if (checkMode(COMMAND_LINE_MODE.CHAR_MODE))
            return;

        charLayer.put(c);
    }

    /**
     * writes the specified text to the commandline
     *
     * @param text the text to write
     */
    public void write(String text) {
        if (checkMode(COMMAND_LINE_MODE.CHAR_MODE))
            return;

        for (char c : text.toCharArray())
            charLayer.put(c);
    }

    /**
     * return the last line created
     * last line =
     * the last line above the cursor,
     * a line is ended by '\n' and starts after the
     * '\n' of the previous line
     *
     * @return the last line in the buffer above the cursor
     */
    public String getLine() {
        if (checkMode(COMMAND_LINE_MODE.CHAR_MODE))
            return null;

        return textLayer.getLastLine();
    }

    /**
     * lists all lines currently in the buffer
     * lines are defined as the text between two '\n'
     * in the buffer
     *
     * @return a list of lines
     */
    public java.util.List<String> listLines() {
        if (checkMode(COMMAND_LINE_MODE.CHAR_MODE))
            return null;

        return textLayer.listLines();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (     //filter backspace and enter
                e.getKeyChar() != '\n' &&
                        e.getKeyChar() != '\b' &&
                        e.getKeyChar() != '\r' &&
                        !e.isActionKey()) {
            charLayer.put(e.getKeyChar());
        }
    }

    ////////////////////////////////////////////////////////////////////////
    // textlayer (used in COMMAND_LINE_MODE.LINE_MODE)
    ////////////////////////////////////////////////////////////////////////

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case VK_UP:
                break;

            case VK_DOWN:
                break;

            case VK_RIGHT:
                break;

            case VK_LEFT:
                break;

            case VK_ENTER:

                break;

            case VK_BACK_SPACE:
                break;
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    // charlayer (used in COMMAND_LINE_MODE.CHAR_MODE)
    /////////////////////////////////////////////////////////////////////////////

    @Override
    public void keyReleased(KeyEvent e) {

    }

    ///////////////////////////////////////////////////////////////////////////////
    // event handling
    ///////////////////////////////////////////////////////////////////////////////

    /**
     * the mode this commandline is running in
     */
    public static enum COMMAND_LINE_MODE {
        /**
         * default mode, the commandline works as usual commandline
         * reading commands, processing them and printing results
         */
        LINE_MODE,
        /**
         * ascii-art mode, the commandline doesn't directly react to
         * keyevents, the only part of the screen which is available
         * is the part currently visible
         */
        CHAR_MODE
    }

    /**
     * the textlayer
     * <p>
     * this layer is used when the mode is LINE_MODE
     */
    private class TextLayer {
        /**
         * the charlayer upon which this textlayer is based
         */
        private CharLayer charLayer;

        /**
         * creates a new textlayer
         */
        public TextLayer() {
            this.charLayer = CommandLineUI.this.charLayer;
        }

        /**
         * @return the lastline of text which was entered
         */
        public String getLastLine() {
            return "";
        }

        /**
         * lists all lines which are currently in the buffer
         *
         * @return a list of lines
         */
        public java.util.List<String> listLines() {
            return null;
        }

        /**
         * writes the specified char to the buffer at the cursorposition
         *
         * @param c the character to write
         */
        public void writeChar(char c) {
            charLayer.put(c);
        }
    }

    /**
     * the basic layer upon which all higher layers are based
     */
    private class CharLayer {
        /**
         * the buffer with all displayed characters
         * <p>
         * the inner arrays represent the lines
         */
        private char[][] screenBuffer;

        /**
         * the widht of the layer in chars
         */
        private int width;

        /**
         * the height of the layer in chars
         */
        private int height;

        /**
         * the cursor position in the line
         */
        private int cursorX;

        /**
         * the line at which the cursor is currently positioned
         * note: line of the buffer, for the actual cursorposition as displayed
         * on screen, use screenCursorY = (cursorY + lineWrapper) % height
         */
        private int cursorY;

        /**
         * linewrapper
         * <p>
         * if the buffer is full, and the program keeps writing, the lineWrapper
         * will increase. in this way, the line that will be overwritten is always the oldest one,
         * without moving the complete buffer
         */
        private int lineWrapper = 0;

        /**
         * creates a new charlayer with the specified width and height
         *
         * @param width  the width of the charlayer
         * @param height the height of the charlayer
         */
        public CharLayer(int width, int height) {
            this.width = width;
            this.height = height;

            screenBuffer = new char[height][width];

            cursorX = 0;
            cursorY = 0;
        }

        /**
         * moves the cursor to the specified position
         * this method automatically calculates the linewrapper aswell
         *
         * @param x the inline position of the cursor
         * @param y the line at which the cursor will be placed
         */
        public void moveCursor(int x, int y) {
            if (x > width || x < 0 || y > height || y < 0)
                throw new IllegalArgumentException("the cursor may not be moved out of the bounds of the console");

            cursorX = x;
            cursorY = (y + lineWrapper) % height;

            repaint();
        }

        /**
         * cleares the specified position
         * this method takes care of the linewrapper
         *
         * @param x inline position in the buffer
         * @param y line onscreen
         */
        public void clear(int x, int y) {
            screenBuffer[y][x] = NO_CHAR;

            repaint();
        }

        /**
         * clears the complete buffer
         */
        public void clearAll() {
            for (char[] line : screenBuffer)
                Arrays.fill(line, NO_CHAR);

            cursorX = 0;
            cursorY = 0;

            repaint();
        }

        /**
         * puts a character at the cursorposition
         * and moves the cursor one to the left, or
         * one line down, if the line is full
         *
         * @param c the character to insert
         */
        public void put(char c) {
            screenBuffer[cursorY][cursorX] = c;

            cursorX++;
            if ((cursorX % width) == 0) {
                cursorX = 0;
                cursorY++;

                if (cursorY > height)
                    lineWrapper++;
            }

            repaint();
        }
    }
}