package dove.desktop.sphere;

import dove.desktop.event.EventRedirect;
import dove.desktop.timer.DesktopScheduler;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;

public class FileSphere
        implements Iterable<SphereFile> {
    private HashMap<Coordinate, SphereFile> files;
    private double           radius;
    private double           moveBorder;
    private DesktopScheduler scheduler;

    public FileSphere(EventRedirect redirect, DesktopScheduler scheduler) {
        files = new HashMap<>();

        this.scheduler = scheduler;

        radius = 1.0;

        redirect.addRedirectTarget((o, id) -> {
            MouseEvent e = (MouseEvent) o;

            switch (e.getID()) {
                case MouseEvent.MOUSE_CLICKED: {
                    if (e.getButton() == MouseEvent.BUTTON1)
                        ;
                    else if (e.getButton() == MouseEvent.BUTTON2)
                        ;
                }
                break;

                case MouseEvent.MOUSE_MOVED: {

                }
                break;
            }
        }, EventRedirect.MOUSEEVENT);
    }

    @Override
    public Iterator<SphereFile> iterator() {
        return files.values().iterator();
    }

    public void add(SphereFile file) {
        files.put(new Coordinate(file.getLongitue(), file.getLatitude()), file);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

}
