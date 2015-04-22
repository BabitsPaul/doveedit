package dove.desktop.controller;

import dove.desktop.event.EventRedirect;
import dove.desktop.event.RedirectTarget;
import dove.desktop.sphere.FileSphere;
import dove.desktop.timer.DesktopScheduler;
import dove.desktop.ui.DesktopFrame;
import dove.desktop.ui.DesktopPane;

import java.awt.event.MouseEvent;

/**
 * Created by Babits on 21/04/2015.
 */
public class DesktopController
        implements RedirectTarget {
    private DesktopFrame     frame;
    private DesktopPane      pane;
    private FileSphere       sphere;
    private DesktopScheduler scheduler;

    public DesktopController(DesktopFrame frame, DesktopPane pane, FileSphere sphere,
                             EventRedirect redirect, DesktopScheduler scheduler) {
        this.frame = frame;
        this.pane = pane;
        this.sphere = sphere;
        this.scheduler = scheduler;

        redirect.addRedirectTarget(this, EventRedirect.MOUSEEVENT);
    }

    @Override
    public void eventCaught(Object o, int id) {
        switch (id) {
            case EventRedirect.MOUSEEVENT: {
                MouseEvent e = (MouseEvent) o;

                switch (e.getID()) {
                    case MouseEvent.MOUSE_MOVED:

                        break;
                }
            }
            break;
        }
    }
}
