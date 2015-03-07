package dove.util.keyredirect;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * redirects all keyevents, which are caught to
 * another keylistener
 */
public class KeyRedirect
        implements KeyListener {
    /**
     * the keylistener to which the keyevents are redirected
     */
    private KeyListener redirectTo;

    /**
     * creates a new keyredirect, which
     * redirects events to an keyadapter,
     * which doesnt react to anything
     */
    public KeyRedirect() {
        redirectTo = new KeyAdapter() {
        };
    }

    /**
     * changes the listener to redirect event to
     * to the specified keylistener
     *
     * @param redirectTo the new keylistener to redirect keyevents to
     */
    public void redirectTo(KeyListener redirectTo) {
        this.redirectTo = redirectTo;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        redirectTo.keyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        redirectTo.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        redirectTo.keyReleased(e);
    }
}