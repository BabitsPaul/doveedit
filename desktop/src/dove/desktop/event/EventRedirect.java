package dove.desktop.event;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;

public class EventRedirect
        implements PopupMenuListener, WindowListener, MouseListener,
        MouseMotionListener, MouseWheelListener, KeyListener, DesktopListener {
    public static final int KEYEVENT        = 0;
    public static final int MOUSEEVENT      = 1;
    public static final int MOUSEWHEELEVENT = 2;
    public static final int POPUPEVENT      = 3;
    public static final int WINDOWEVENT     = 4;
    public static final int DESKTOPEVENT    = 5;

    private HashMap<Integer, ArrayList<RedirectTarget>> redirectTo;

    public EventRedirect() {
        redirectTo = new HashMap<>();

        redirectTo.put(KEYEVENT, new ArrayList<>());
        redirectTo.put(MOUSEEVENT, new ArrayList<>());
        redirectTo.put(MOUSEWHEELEVENT, new ArrayList<>());
        redirectTo.put(POPUPEVENT, new ArrayList<>());
        redirectTo.put(WINDOWEVENT, new ArrayList<>());
        redirectTo.put(DESKTOPEVENT, new ArrayList<>());
    }

    public void addRedirectTarget(RedirectTarget target, int eventtype) {
        redirectTo.get(eventtype).add(target);
    }

    public void removeRedirectTarget(RedirectTarget target, int eventtype) {
        redirectTo.get(eventtype).remove(target);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        redirectEvent(e, MOUSEEVENT);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        redirectEvent(e, MOUSEEVENT);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        redirectEvent(e, MOUSEEVENT);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        redirectEvent(e, MOUSEEVENT);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        redirectEvent(e, MOUSEEVENT);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        redirectEvent(e, MOUSEEVENT);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        redirectEvent(e, MOUSEEVENT);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        redirectEvent(e, MOUSEWHEELEVENT);
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        redirectEvent(e, POPUPEVENT);
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        redirectEvent(e, POPUPEVENT);
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
        redirectEvent(e, POPUPEVENT);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        redirectEvent(e, WINDOWEVENT);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        redirectEvent(e, WINDOWEVENT);
    }

    @Override
    public void windowClosed(WindowEvent e) {
        redirectEvent(e, WINDOWEVENT);
    }

    @Override
    public void windowIconified(WindowEvent e) {
        redirectEvent(e, WINDOWEVENT);
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        redirectEvent(e, WINDOWEVENT);
    }

    @Override
    public void windowActivated(WindowEvent e) {
        redirectEvent(e, WINDOWEVENT);
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        redirectEvent(e, WINDOWEVENT);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        redirectEvent(e, KEYEVENT);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        redirectEvent(e, KEYEVENT);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        redirectEvent(e, KEYEVENT);
    }

    @Override
    public void desktopChanged(DesktopEvent e) {
        redirectEvent(e, DESKTOPEVENT);
    }

    private void redirectEvent(Object e, int id) {
        redirectTo.get(id).forEach(t -> t.eventCaught(e, id));
    }
}
