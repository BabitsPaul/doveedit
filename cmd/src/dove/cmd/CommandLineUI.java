package dove.cmd;

import dove.cmd.model.AbstractCommandLayer;
import dove.cmd.model.CharBuffer;
import dove.cmd.model.CommandLineCursor;
import dove.cmd.model.TextLayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

public class CommandLineUI
        extends JPanel
        implements HierarchyListener {

    /**
     * the console font
     */
    private static final Font COMMAND_FONT = new Font("Monospaced", Font.PLAIN, 12);

    private static final int PAINT_OFFSET_TOP    = 3;
    private static final int PAINT_OFFSET_LEFT   = 3;
    private static final int PAINT_OFFSET_RIGHT  = 3;
    private static final int PAINT_OFFSET_BOTTOM = 3;

    private static final int LINE_SPACE = 2;

    private CommandLineCursor    cursor;
    private CharBuffer           buffer;
    private UI_MODE              mode;
    private AbstractCommandLayer activeLayer;
    private int charWidth;
    private int charHeight;
    public CommandLineUI() {
        activeLayer = new TextLayer();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        char[][] buffer = this.buffer.getContent();

        Color[][] colors = this.buffer.getColors();

        int lineHeight = charHeight + LINE_SPACE;

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

        g.setColor(cursorBackground);
        g.fillRect(cursorX * charWidth + PAINT_OFFSET_LEFT, cursorY * lineHeight + PAINT_OFFSET_TOP,
                charWidth, charHeight);
        g.setColor(cursorForeground);
        g.drawChars(cursorContent, 0, 1,
                cursorX * charWidth + PAINT_OFFSET_LEFT, (cursorY + 1) * lineHeight + PAINT_OFFSET_TOP);
    }

    public AbstractCommandLayer getActiveLayer() {
        return activeLayer;
    }

    //////////////////////////////////////////////////////////
    // hierachylistener
    //////////////////////////////////////////////////////////

    /**
     * update uirelated values
     *
     * @param e the hierachyevent
     */
    @Override
    public void hierarchyChanged(HierarchyEvent e) {
        if (e.getID() == HierarchyEvent.PARENT_CHANGED) {
            charWidth = getGraphics().getFontMetrics().charWidth(' ');
            charHeight = getGraphics().getFontMetrics().getHeight();
        }
    }

    public enum UI_MODE {
        SINGLE_SIGN_MODE,
        TEXT_MODE
    }
}