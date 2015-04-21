package dove.desktop;

import dove.desktop.event.EventRedirect;
import dove.desktop.loader.SphereLoader;
import dove.desktop.sphere.FileSphere;
import dove.desktop.timer.DesktopScheduler;
import dove.desktop.ui.DesktopFrame;
import dove.desktop.ui.DesktopPane;

import java.io.IOException;

/**
 * Created by Babits on 16/04/2015.
 */
public class DoveDesktop {
    public static void main(String[] args)
            throws IOException {
        EventRedirect redirect = new EventRedirect();
        DesktopScheduler scheduler = new DesktopScheduler();
        FileSphere sphere = new SphereLoader(redirect, scheduler).load();
        DesktopPane pane = new DesktopPane(redirect, sphere);
        DesktopFrame frame = new DesktopFrame(pane, redirect);

        frame.setVisible(true);
    }
}
