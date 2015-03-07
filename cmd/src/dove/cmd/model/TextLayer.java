package dove.cmd.model;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static java.awt.event.KeyEvent.*;

public class TextLayer
        extends AbstractCommandLayer {
    private CharBuffer buffer;

    private CommandLineCursor cursor;

    public TextLayer(CommandLineCursor cursor, CharBuffer buffer) {
        super(null);

        //set the keyredirect to redirect event to this instance
        redirectTo(new KeyHelper());

        this.buffer = buffer;
        this.cursor = cursor;
    }

    @Override
    public void enableLayer() {
        buffer.reverseClip();
        cursor.setVisible(true);
    }

    ////////////////////////////////////////////////////////////
    // lines
    ////////////////////////////////////////////////////////////

    public String getLastLine() {
        return "";
    }

    public void write(String text) {

    }

    public void writeln(String text) {
        write(text + "\n");
    }

    /////////////////////////////////////////////////////////////
    // keylistener
    /////////////////////////////////////////////////////////////

    private class KeyHelper
            implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case VK_UP:
                    cursor.moveCursorUp();
                    break;

                case VK_DOWN:
                    cursor.moveCursorDown();
                    break;

                case VK_LEFT:
                    cursor.moveCursorLeft();
                    break;

                case VK_RIGHT:
                    cursor.moveCursorRight();
                    break;

                case VK_BACK_SPACE:
                    buffer.put(NO_CHAR);
                    cursor.moveCursorLeft();
                    cursor.moveCursorLeft();
                    break;

                case VK_ENTER:
                    //TODO enter new line
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}