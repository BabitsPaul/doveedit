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

    public void fireEvent(Object o) {
        if (o instanceof MouseEvent) {
            MouseEvent e = (MouseEvent) o;

            switch (e.getID()) {
                case MouseEvent.MOUSE_PRESSED:
                    mousePressed(e);
                    break;
                case MouseEvent.MOUSE_CLICKED:
                    mouseClicked(e);
                    break;
                case MouseEvent.MOUSE_RELEASED:
                    mouseReleased(e);
                    break;

                case MouseEvent.MOUSE_MOVED:
                    mouseMoved(e);
                    break;
                case MouseEvent.MOUSE_DRAGGED:
                    mouseDragged(e);
                    break;
                case MouseEvent.MOUSE_ENTERED:
                    mouseEntered(e);
                    break;
                case MouseEvent.MOUSE_EXITED:
                    mouseExited(e);
                    break;
            }
        }
        else if (o instanceof KeyEvent) {
            KeyEvent e = (KeyEvent) o;

            switch (e.getID()) {
                case KeyEvent.KEY_PRESSED:
                    keyPressed(e);
                    break;
                case KeyEvent.KEY_TYPED:
                    keyTyped(e);
                    break;
                case KeyEvent.KEY_RELEASED:
                    keyReleased(e);
                    break;
            }
        }
        else if (o instanceof MouseWheelEvent) {
            mouseWheelMoved((MouseWheelEvent) o);
        }
        else if (o instanceof DesktopEvent) {
            desktopChanged((DesktopEvent) o);
        }
        else if (o instanceof DesktopEvent) {
            desktopChanged((DesktopEvent) o);
        }
        else if (o instanceof WindowEvent) {
            WindowEvent e = (WindowEvent) o;

            switch (e.getID()) {
                case WindowEvent.WINDOW_CLOSED:
                    windowClosed(e);
                    break;
                case WindowEvent.WINDOW_CLOSING:
                    windowClosing(e);
                    break;
                case WindowEvent.WINDOW_ACTIVATED:
                    windowActivated(e);
                    break;
                case WindowEvent.WINDOW_DEACTIVATED:
                    windowDeactivated(e);
                    break;
                case WindowEvent.WINDOW_ICONIFIED:
                    windowIconified(e);
                    break;
                case WindowEvent.WINDOW_DEICONIFIED:
                    windowDeiconified(e);
                    break;
                case WindowEvent.WINDOW_OPENED:
                    windowOpened(e);
                    break;
            }
        }
        else
            throw new IllegalArgumentException("Invalid input");
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
