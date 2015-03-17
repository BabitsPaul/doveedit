package dove.cmd.ui;

import dove.cmd.interpreter.CommandLineInterpreter;
import dove.cmd.ui.model.*;
import dove.cmd.ui.paint.AbstractLayerRenderer;
import dove.cmd.ui.paint.LayerRendererMetrics;
import dove.cmd.ui.paint.RendererMetricsFactory;
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
    private InternalCursor     cursor;
    private InternalCharBuffer buffer;
    private UI_MODE                mode;
    private AbstractCommandLayer   activeLayer;
    private int                    charWidth;
    private int                    charHeight;
    private Ticker                 cursorTicker;
    private CommandLineInterpreter interpreter;
    private boolean                paintCursor;
    private AbstractLayerRenderer renderer;
    private RendererMetricsFactory metricsFactory;

    public CommandLineUI(int width, int height) {
        //create renderer metrics factory
        metricsFactory = new RendererMetricsFactory();
        metricsFactory.setTextSpaceTop(PAINT_OFFSET_TOP);
        metricsFactory.setTextSpaceBottom(PAINT_OFFSET_BOTTOM);
        metricsFactory.setTextSpaceLeft(PAINT_OFFSET_LEFT);
        metricsFactory.setTextSpaceRight(PAINT_OFFSET_RIGHT);
        metricsFactory.setLineSpace(LINE_SPACE);
        metricsFactory.setBackground(getBackground());
        metricsFactory.setShowCursor(true);
        metricsFactory.setSignSpace(0);

        //initialize interpreter (NOTE: remove initInterpreter later - values are transferred to commands)
        interpreter = new CommandLineInterpreter();
        initInterpreter();

        //initialize cursor
        cursor = new InternalCursor(width, height);

        //create a tickerinstance to make the cursor flash
        cursorTicker = new Ticker((Long) interpreter.get("commandline.cursor.freq")) {
            @Override
            protected void nextTick() {
                paintCursor = !paintCursor;

                metricsFactory.setShowCursor(paintCursor);

                repaint();
            }
        };

        //initialize buffer
        buffer = new InternalCharBuffer(width, height, cursor, Color.BLUE);

        //initialize layer
        setMode(UI_MODE.TEXT_MODE);

        cursorTicker.start();
        cursor.addCommandLineListener(this);
        buffer.addCommandLineListener(this);

        //painting
        setDoubleBuffered(true);

        //react to keyinput
        setFocusable(true);
        requestFocus();
    }

    //create a basic interpreter
    //will be replaced by a commandrelated
    //initialisation later
    private void initInterpreter() {
        interpreter.put("commandline.cursor.freq", 500L);
        interpreter.put("commandline.color.foreground", Color.WHITE);
        interpreter.put("commandline.color.background", Color.BLACK);
    }

    public void setMetrics(LayerRendererMetrics metrics) {
        metricsFactory.useMetrics(metrics);

        repaint();
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
        metricsFactory.setSignWidth(charWidth);
        metricsFactory.setLineHeight(charHeight);
        metricsFactory.setSignMaxAscent(getFontMetrics(COMMAND_FONT).getMaxAscent());
        metricsFactory.setBackground(getBackground());

        return metricsFactory.createMetrics();
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

            metricsFactory.setLineSpace(LINE_SPACE);
        }
        else {
            AbstractCommandLayer prevLayer = activeLayer;

            activeLayer = new TextLayer(cursor, buffer);

            metricsFactory.setLineSpace(0);

            //move cursor to the end of the previous layer
            //if the previous layer was a charlayer
            if (mode.equals(UI_MODE.SINGLE_SIGN_MODE)) {
                int offset = ((CharLayer) prevLayer).getyOffSet();
                int height = ((CharLayer) prevLayer).getHeight();

                cursor.setY(offset + height);
            }
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
        switch (e.getSourceType()) {
            case CURSOR_TYPE:
                cursorTicker.enforceTick();
                break;
        }

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