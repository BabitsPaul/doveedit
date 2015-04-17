package dove.desktop.ui;

import dove.desktop.event.DesktopEvent;
import dove.desktop.event.EventRedirect;
import dove.desktop.sphere.FileSphere;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Babits on 16/04/2015.
 */
public class DesktopPane
        extends JPanel {
    private FileSphere sphere;


    public DesktopPane(EventRedirect redirect) {
        try {
            sphere = FileSphere.createInstance(redirect);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Corrupted configurationfile", "ERROR", JOptionPane.ERROR_MESSAGE);

            sphere = new FileSphere(redirect);
        }

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
