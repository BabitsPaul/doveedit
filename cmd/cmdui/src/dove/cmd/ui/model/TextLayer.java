package dove.cmd.ui.model;

import dove.cmd.ui.paint.AbstractLayerRenderer;
import dove.cmd.ui.paint.TextLayerRenderer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static java.awt.event.KeyEvent.*;

public class TextLayer
        extends AbstractCommandLayer {
    private AbstractTextLayerModel model;

    public TextLayer(Cursor cursor, CharBuffer buffer, AbstractTextLayerModel model) {
        super(null, cursor, buffer);

        //set the keyredirect to redirect event to this instance
        redirectTo(new KeyHelper());

        this.model = model;
    }

    public AbstractTextLayerModel getModel() {
        return model;
    }

    public void setModel(AbstractTextLayerModel nModel) {
        this.model = nModel;
    }

    /////////////////////////////////////////////////////////////////
    // painting
    /////////////////////////////////////////////////////////////////

    @Override
    public AbstractLayerRenderer createRenderer() {
        return new TextLayerRenderer(this);
    }

    /////////////////////////////////////////////////////////////
    // keylistener
    /////////////////////////////////////////////////////////////

    private class KeyHelper
            implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            model.addChar(e.getKeyChar());
        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case VK_UP:
                    model.cursorUp();
                    break;

                case VK_DOWN:
                    model.cursorDown();
                    break;

                case VK_LEFT:
                    model.cursorLeft();
                    break;

                case VK_RIGHT:
                    model.cursorRight();
                    break;

                case VK_BACK_SPACE:
                    model.removeChar();
                    break;

                case VK_ENTER:
                    model.nextLine();
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}