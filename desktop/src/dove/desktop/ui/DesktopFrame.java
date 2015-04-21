package dove.desktop.ui;

import dove.desktop.event.DesktopEvent;
import dove.desktop.event.EventRedirect;

import javax.swing.*;

public class DesktopFrame
        extends JFrame {
    public DesktopFrame(DesktopPane pane, EventRedirect redirect) {
        super("Dove Desktop");

        addWindowListener(redirect);
        redirect.addRedirectTarget((o, id) -> {
            DesktopEvent e = (DesktopEvent) o;

            if (e.getCode() == DesktopEvent.DESKTOP_CLOSING) {
                setVisible(false);
                dispose();
            }
        }, EventRedirect.DESKTOPEVENT);

        setContentPane(pane);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(MAXIMIZED_BOTH);
        setResizable(false);
    }
}
