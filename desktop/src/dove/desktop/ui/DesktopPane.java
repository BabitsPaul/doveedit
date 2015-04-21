package dove.desktop.ui;

import dove.desktop.event.DesktopEvent;
import dove.desktop.event.EventRedirect;
import dove.desktop.loader.SphereLoader;
import dove.desktop.sphere.FileSphere;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by Babits on 16/04/2015.
 */
public class DesktopPane
        extends JPanel {
    private FileSphere sphere;

    public DesktopPane(EventRedirect redirect, FileSphere sphere) {
        this.sphere = sphere;

        redirect.addRedirectTarget((Object o, int id) -> {
            DesktopEvent e = (DesktopEvent) o;

            if (e.getCode() == DesktopEvent.DESKTOP_CLOSING)
                try {
                    new SphereLoader(redirect).save(sphere);
                }
                catch (IOException ex) {
                }
        }, EventRedirect.DESKTOPEVENT);

        addKeyListener(redirect);
        addMouseListener(redirect);
        addMouseWheelListener(redirect);
        addMouseMotionListener(redirect);

        JMenu popupContent = new JMenu("Hello world");
        popupContent.add(new JMenuItem("asdf"));

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> redirect.desktopChanged(new DesktopEvent(DesktopEvent.DESKTOP_CLOSING)));

        JPopupMenu popup = new JPopupMenu();
        popup.setLayout(new BoxLayout(popup, BoxLayout.Y_AXIS));
        popup.add(popupContent);
        popup.add(exit);
        popup.setLayout(new GridLayout(1, 1));
        popup.addPopupMenuListener(redirect);
        setComponentPopupMenu(popup);
    }
}
