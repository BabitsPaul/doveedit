package dove.cmd.ui;

import dove.cmd.ui.model.*;
import dove.cmd.ui.model.Cursor;
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
    private static final Font COMMAND_FONT = new Font("Monospaced", Font.PLAIN, 12);

    /**
     * the default topspace for painting text
     */
    private static final int PAINT_OFFSET_TOP = 3;

    /**
     * the default space on the left for painting text
     */
    private static final int PAINT_OFFSET_LEFT = 3;

    /**
     * the default space on the right for painting text
     */
    private static final int PAINT_OFFSET_RIGHT = 3;

    /**
     * the default space on the bottom for painting text
     */
    private static final int PAINT_OFFSET_BOTTOM = 3;

    /**
     * the space between lines
     */
    private static final int LINE_SPACE = 2;

    /**
     * the cursor for this ui
     */
    private Cursor cursor;

    /**
     * the buffer used by this ui and its model
     */
    private CharBuffer buffer;

    /**
     * the currently active layer
     */
    private AbstractCommandLayer activeLayer;

    /**
     * the width of a char
     */
    private int charWidth;

    /**
     * the height of a char
     */
    private int charHeight;

    /**
     * the ticker for making the cursor blink
     */
    private Ticker cursorTicker;

    /**
     * true, if the cursor should be painted (used by cursorTicker)
     */
    private boolean paintCursor;

    /**
     * the renderer related to the current active layer
     */
    private AbstractLayerRenderer renderer;

    /**
     * the metricsfactory related to this ui
     */
    private RendererMetricsFactory metricsFactory;

    /**
     * the clipobject that handles clipping for this ui
     */
    private ClipObject clip;

    /**
     * false, if repaint due to events is blocked by a component
     */
    private boolean repaintOnEvent;

    /**
     * the configuration related to this ui
     */
    private CmdUIConfiguration configuration;

    /**
     * creates a new commandline terminal with the specified width
     * and height
     *
     * @param width  the width of the buffer
     * @param height the height of the buffer
     */
    public CommandLineUI(int width, int height) {
        repaintOnEvent = true;

        //create configuration
        configuration = new CmdUIConfiguration();
        initCfg();

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

        //initialize the clipping object
        clip = new ClipObject(width, height);
        clip.addCommandLineListener(this);

        //initialize cursor
        cursor = new Cursor(clip);
        cursor.setVisible(true);
        cursor.addCommandLineListener(this);

        //initialize buffer
        buffer = new CharBuffer(width, height, cursor, Color.BLUE, clip);
        buffer.addCommandLineListener(this);

        //initialize layer
        activeLayer = new TextLayer(cursor, buffer, new DefaultTextLayerModel(buffer, cursor, clip));
        addKeyListener(activeLayer);
        renderer = activeLayer.createRenderer();

        //create a tickerinstance to make the cursor flash
        cursorTicker = new Ticker((Long) configuration.get("commandline.cursor.freq")) {
            @Override
            protected void nextTick() {
                paintCursor = !paintCursor;

                metricsFactory.setShowCursor(paintCursor);

                repaint();
            }
        };
        cursorTicker.start();

        //painting
        setDoubleBuffered(true);

        //react to keyinput
        setFocusable(true);
        requestFocus();
    }

    //create a basic interpreter
    //will be replaced by a .cfg file later
    private void initCfg() {
        configuration.put("commandline.cursor.freq", 500L);
        configuration.put("commandline.color.foreground", Color.WHITE);
        configuration.put("commandline.color.background", Color.BLACK);
    }

    /**
     * changes the defaultmetrics, from which the
     * metrics used to render the ui are derived
     *
     * @param metrics the new metrics for rendering this ui
     */
    public void setMetrics(LayerRendererMetrics metrics) {
        metricsFactory.useMetrics(metrics);

        repaint();
    }

    /**
     * paints this component
     *
     * @param g a graphics object
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setFont(COMMAND_FONT);

        charWidth = g.getFontMetrics().charWidth(' ');
        charHeight = g.getFontMetrics().getHeight();

        renderer.renderLayer(g, createMetrics());
    }

    /**
     * creates a new metrics object using the
     * metricsfactory
     *
     * @return a metricsobject
     */
    private LayerRendererMetrics createMetrics() {
        metricsFactory.setSignWidth(charWidth);
        metricsFactory.setLineHeight(charHeight);
        metricsFactory.setSignMaxAscent(getFontMetrics(COMMAND_FONT).getMaxAscent());
        metricsFactory.setBackground(getBackground());

        return metricsFactory.createMetrics();
    }

    //////////////////////////////////////////////////////////
    // getters
    //////////////////////////////////////////////////////////

    public CharBuffer getBuffer() {
        return buffer;
    }

    public Cursor getCmdCursor() {
        return cursor;
    }

    public ClipObject getClip() {
        return clip;
    }

    //////////////////////////////////////////////////////////
    // metrics
    //////////////////////////////////////////////////////////

    public Dimension getSize() {
        return renderer.getSize(createMetrics());
    }

    public void setSize(Dimension size) {
        //throw new IllegalStateException("This component can't be resized");
    }

    public Dimension getMinimumSize() {
        return renderer.getSize(createMetrics());
    }

    public Dimension getPreferredSize() {
        return renderer.getSize(createMetrics());
    }

    public void setSize(int width, int height) {
        //throw new IllegalStateException("This component can't be resized");
    }

    /////////////////////////////////////////////////////////
    // mode related operations
    /////////////////////////////////////////////////////////

    public AbstractCommandLayer getActiveLayer() {
        return activeLayer;
    }

    public void setModel(AbstractCharLayerModel model) {
        AbstractCommandLayer newLayer = new CharLayer(model, buffer, cursor);

        int freeLine = lastUsedLine();

        addKeyListener(newLayer);
        removeKeyListener(activeLayer);

        activeLayer = newLayer;

        renderer = activeLayer.createRenderer();

        revalidate();
        repaint();
    }

    public void setModel(AbstractTextLayerModel model) {
        //TODO several issues (rendering + input)
        AbstractCommandLayer newLayer = new TextLayer(cursor, buffer, model);

        metricsFactory.setLineSpace(0);

        int freeLine = lastUsedLine();
        if (freeLine == -1) {
            buffer.pushContentUp(20);

            freeLine = buffer.getHeight() - 20;
        }

        clip.reverseClipping();
        cursor.setVisible(true);

        cursor.setY(freeLine);

        addKeyListener(newLayer);
        removeKeyListener(activeLayer);

        activeLayer = newLayer;

        renderer = activeLayer.createRenderer();

        revalidate();
        repaint();
    }

    private int lastUsedLine() {
        //TODO invalid result
        if (clip.isEnabled()) {
            return clip.getOffSetX() + clip.getHeight();
        }
        else {
            int line = buffer.getHeight() - 1;
            char[][] buffer = this.buffer.getContent();

            for (; line > -1; line--) {
                boolean isEmpty = true;

                for (char c : buffer[line])
                    if (!(isEmpty = (c == AbstractCommandLayer.NO_CHAR)))
                        break;

                if (!isEmpty)
                    return (line == this.buffer.getHeight() - 1 ? -1 : line);
            }

            return 0;
        }
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

            case PAINTING:
                switch (e.getModifier()) {
                    case CommandLineEvent.ENABLE_EVENT_RELATED_REPAINT:
                        repaintOnEvent = true;
                        break;

                    case CommandLineEvent.SUPPRESS_EVENT_RELATED_REPAINT:
                        repaintOnEvent = false;
                        break;
                }
                break;

            default:
                if (repaintOnEvent)
                    repaint();
        }
    }
}