package dove.cmd.ui;

import dove.cmd.interpreter.CommandLineInterpreter;
import dove.cmd.ui.model.*;
import dove.cmd.ui.paint.AbstractLayerRenderer;
import dove.cmd.ui.paint.LayerRendererMetrics;
import dove.util.concurrent.Ticker;

import javax.swing.*;
import java.awt.*;

public class CommandLineUI
        extends JComponent
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
    private AbstractLayerRenderer renderer;

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
    private void initInterpreter() {
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

        renderer.renderLayer(g, createMetrics());
    }

    private LayerRendererMetrics createMetrics() {
        LayerRendererMetrics metrics = new LayerRendererMetrics();

        metrics.signWidth = charWidth;
        metrics.lineHeight = charHeight;
        metrics.lineSpace = LINE_SPACE;
        metrics.signSpace = 0;
        metrics.textSpaceLeft = PAINT_OFFSET_LEFT;
        metrics.textSpaceRight = PAINT_OFFSET_RIGHT;
        metrics.textSpaceTop = PAINT_OFFSET_TOP;
        metrics.textSpaceBottom = PAINT_OFFSET_BOTTOM;
        metrics.showCursor = paintCursor;
        metrics.background = getBackground();
        metrics.signMaxAscent = getFontMetrics(COMMAND_FONT).getMaxAscent();

        return metrics;
    }

    //////////////////////////////////////////////////////////
    // metrics
    //////////////////////////////////////////////////////////

    public Dimension getSize() {
        return renderer.getSize(createMetrics());
    }

    public void setSize(Dimension size) {
        throw new IllegalStateException("This component can't be resized");
    }

    public Dimension getMinimumSize() {
        return renderer.getSize(createMetrics());
    }

    public Dimension getPreferredSize() {
        return renderer.getSize(createMetrics());
    }

    public void setSize(int width, int height) {
        throw new IllegalStateException("This component can't be resized");
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

        renderer = activeLayer.createRenderer();

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