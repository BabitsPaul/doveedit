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

        char[][] buffer = this.buffer.getContent();

        Color[][] colors = this.buffer.getColors();

        int lineHeight = charHeight + 2;

        for (int line = 0; line < buffer.length; line++)
            for (int col = 0; col < buffer[i].length; col++) {
                if (buffer[line][col] == AbstractCommandLayer.NO_CHAR)
                    continue;

                g.drawChars(new char[]{buffer[line][col]}, 0, 1, col * charWidth + 3, line * lineHeight);
            }
    }

    public AbstractCommandLayer getActiveLayer() {
        return activeLayer;
    }

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

    //////////////////////////////////////////////////////////
    // hierachylistener
    //////////////////////////////////////////////////////////

    public enum UI_MODE {
        SINGLE_SIGN_MODE,
        TEXT_MODE
    }
}