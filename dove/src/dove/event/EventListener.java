package dove.event;

import dove.api.FrameApi;
import dove.document.DocumentContext;

import java.awt.event.*;

public class EventListener
        implements ComponentListener,
        MouseListener,
        MouseMotionListener,
        MouseWheelListener,
        KeyListener,
        WindowListener {
    private FrameApi proc;

    public EventListener(DocumentContext doc) {
        proc = doc.frame;

        proc.setupListener(this);

        doc.event = this;
    }


    //////////////////////////////////////////////////////////
    // componentlistener
    //////////////////////////////////////////////////////////
    @Override
    public void componentResized(ComponentEvent e) {
        proc.process(new EventRep(e));
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        proc.process(new EventRep(e));
    }

    @Override
    public void componentShown(ComponentEvent e) {
        proc.process(new EventRep(e));
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        proc.process(new EventRep(e));
    }

    /////////////////////////////////////////////////////////
    // keylistener
    /////////////////////////////////////////////////////////
    @Override
    public void keyTyped(KeyEvent e) {
        proc.process(new EventRep(e));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        proc.process(new EventRep(e));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        proc.process(new EventRep(e));
    }

    //////////////////////////////////////////////////////////////
    // mouselistener
    //////////////////////////////////////////////////////////////
    @Override
    public void mouseClicked(MouseEvent e) {
        proc.process(new EventRep(e));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        proc.process(new EventRep(e));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        proc.process(new EventRep(e));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        proc.process(new EventRep(e));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        proc.process(new EventRep(e));
    }

    ///////////////////////////////////////////////////////
    // mousemotionlistener
    ///////////////////////////////////////////////////////
    @Override
    public void mouseDragged(MouseEvent e) {
        proc.process(new EventRep(e));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        proc.process(new EventRep(e));
    }

    /////////////////////////////////////////////////////////
    // mousewheellistener
    /////////////////////////////////////////////////////////
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        proc.process(new EventRep(e));
    }

    /////////////////////////////////////////////////////////
    // windowlistener
    /////////////////////////////////////////////////////////

    @Override
    public void windowOpened(WindowEvent e) {
        proc.process(new EventRep(e));
    }

    @Override
    public void windowClosing(WindowEvent e) {
        proc.process(new EventRep(e));
    }

    @Override
    public void windowClosed(WindowEvent e) {
        proc.process(new EventRep(e));
    }

    @Override
    public void windowIconified(WindowEvent e) {
        proc.process(new EventRep(e));
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        proc.process(new EventRep(e));
    }

    @Override
    public void windowActivated(WindowEvent e) {
        proc.process(new EventRep(e));
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        proc.process(new EventRep(e));
    }
}
