package dove.cmd.ui;

import dove.cmd.interpreter.CommandLineInterpreter;
import dove.cmd.ui.model.*;
import dove.util.concurrent.Ticker;

import javax.swing.*;
import java.awt.*;

public class CommandLineUI
        extends JPanel
        implements CommandLineUIListener {
    /**
     * the console font
     */
    private static final Font COMMAND_FONT        = new Font("Monospaced", Font.PLAIN, 12);
    private static final int  PAINT_OFFSET_TOP    = 3;
    private static final int  PAINT_OFFSET_LEFT   = 3;
    private static final int  PAINT_OFFSET_RIGHT  = 3;
    private static final int  PAINT_OFFSET_BOTTOM = 3;
    private static final int  LINE_SPACE          = 2;
    private CommandLineCursor      cursor;
    private CharBuffer             buffer;
    private UI_MODE                mode;
    private AbstractCommandLayer   activeLayer;
    private int                    charWidth;
    private int                    charHeight;
    private Ticker                 cursorTicker;
    private CommandLineInterpreter interpreter;
    private boolean                paintCursor;

    public CommandLineUI(int width, int height) {
        //initialize cursor
        cursor = new CommandLineCursor(width, height);
        cursor.addCommandLineListener(this);

        //initialize buffer
        buffer = new CharBuffer(width, height, cursor, Color.BLUE);
        buffer.addCommandLineListener(this);

        //initialize layer
        setMode(UI_MODE.TEXT_MODE);

        //initialize interpreter (NOTE: remove initInterpreter later - values are transferred to commands)
        interpreter = new CommandLineInterpreter();
        initInterpreter();

        //create a tickerinstance to make the cursor flash
        cursorTicker = new Ticker((Long) interpreter.get("commandline.cursor.freq")) {
            @Override
            protected void nextTick() {
                paintCursor = !paintCursor;

                repaint();
            }
        };
        cursorTicker.start();

        //react to keyinput
        setFocusable(true);
        requestFocus();
    }

    //create a basic interpreter
    //will be replaced by a commandrelated
    //initialisation later
    private final void initInterpreter() {
        interpreter.put("commandline.cursor.freq", 500L);
        interpreter.put("commandline.color.foreground", Color.BLACK);
        interpreter.put("commandline.color.background", Color.WHITE);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setFont(COMMAND_FONT);

        charWidth = g.getFontMetrics().charWidth(' ');
        charHeight = g.getFontMetrics().getHeight();

        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        char[][] buffer = this.buffer.getContent();

        Color[][] colors = this.buffer.getColors();

        int lineHeight = charHeight + LINE_SPACE;//TODO value correction

        for (int line = 0; line < buffer.length; line++)
            for (int col = 0; col < buffer[line].length; col++) {
                if (buffer[line][col] == AbstractCommandLayer.NO_CHAR)
                    continue;

                g.setColor(colors[line][col]);
                g.drawChars(new char[]{buffer[line][col]}, 0, 1,
                        col * charWidth + PAINT_OFFSET_LEFT, (line + 1) * lineHeight + PAINT_OFFSET_TOP);
            }

        int cursorX = cursor.getX();
        int cursorY = cursor.getY();
        Color cursorBackground = colors[cursorY][cursorX];
        Color cursorForeground = getBackground();
        char[] cursorContent = new char[]{buffer[cursorY][cursorX]};

        if (paintCursor) {
            g.setColor(cursorBackground);
            g.fillRect(cursorX * charWidth + PAINT_OFFSET_LEFT, cursorY * lineHeight + PAINT_OFFSET_TOP,
                    charWidth, charHeight);

            g.setColor(cursorForeground);
            g.drawChars(cursorContent, 0, 1,
                    cursorX * charWidth + PAINT_OFFSET_LEFT, (cursorY + 1) * lineHeight + PAINT_OFFSET_TOP);
        }
    }

    /////////////////////////////////////////////////////////
    // mode related operations
    /////////////////////////////////////////////////////////

    public AbstractCommandLayer getActiveLayer() {
        return activeLayer;
    }

    public void setMode(UI_MODE mode) {
        if (mode.equals(UI_MODE.SINGLE_SIGN_MODE)) {
            //TODO create valid bounds
            activeLayer = new CharLayer(null, buffer, cursor, 0, 0, 0, 0);
        }
        else {
            //TODO move cursor to the end of the previous layer
            //if the layer was a charlayer
            activeLayer = new TextLayer(cursor, buffer);
        }

        activeLayer.enableLayer();
        addKeyListener(activeLayer);

        this.mode = mode;

        revalidate();
        repaint();
    }

    //////////////////////////////////////////////////////////
    // interpreter
    //////////////////////////////////////////////////////////

    public CommandLineInterpreter getInterpreter() {
        return interpreter;
    }

    ///////////////////////////////////////////////////////////
    // commandlineuilistener
    ///////////////////////////////////////////////////////////


    @Override
    public void commandLineChanged(CommandLineEvent e) {
        repaint();
    }

    /**
     * the mode of the ui
     * these modes are related to abstractcommandlayers
     */
    public enum UI_MODE {
        SINGLE_SIGN_MODE,
        TEXT_MODE
    }
}