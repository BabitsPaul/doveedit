package dove.desktop;

import dove.desktop.controller.DesktopController;
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
        SphereLoader loader = new SphereLoader();
        loader.load();
        FileSphere sphere = new FileSphere(redirect, scheduler);
        DesktopPane pane = new DesktopPane(redirect, sphere);
        DesktopFrame frame = new DesktopFrame(pane, redirect);
        DesktopController controller = new DesktopController(frame, pane, sphere, redirect, scheduler);

        frame.setVisible(true);
    }
}
