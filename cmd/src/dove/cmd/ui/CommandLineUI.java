package dove.cmd.ui;

import javax.swing.*;
import java.awt.*;

public class CommandLineUI
        extends JPanel {
    /**
     * the console font
     */
    private static final Font COMMAND_FONT = new Font("Consolas", Font.PLAIN, 12);
    private CommandLineCursor    cursor;
    private CharBuffer           buffer;
    private UI_MODE              mode;
    private AbstractCommandLayer activeLayer;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        char[][] buffer = this.buffer.getContent();
    }

    public enum UI_MODE {
        SINGLE_SIGN_MODE,
        TEXT_MODE
    }
}