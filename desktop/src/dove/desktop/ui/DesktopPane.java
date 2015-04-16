package dove.desktop.ui;

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

    private JMenu popupContent;

    public DesktopPane(EventRedirect redirect) {
        try {
            sphere = FileSphere.createInstance(redirect);
        }
        catch (IOException e) {

        }

        popupContent = new JMenu("Hello world");
        popupContent.add(new JMenuItem("asdf"));

        JPopupMenu popup = new JPopupMenu();
        popup.add(popupContent);
        popup.setLayout(new GridLayout(1, 1));
        popup.addPopupMenuListener(redirect);
        setComponentPopupMenu(popup);
        getComponentPopupMenu().setPopupSize(100, 300);
    }
}
