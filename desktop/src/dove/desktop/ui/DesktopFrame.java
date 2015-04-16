package dove.desktop.ui;

import javax.swing.*;

/**
 * Created by Babits on 16/04/2015.
 */
public class DesktopFrame
        extends JFrame {
    public DesktopFrame() {
        super("Dove Desktop");

        EventRedirect redirect = new EventRedirect();

        addWindowListener(redirect);

        setContentPane(new DesktopPane(redirect));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(MAXIMIZED_BOTH);
        setResizable(false);
        setVisible(true);
    }
}
