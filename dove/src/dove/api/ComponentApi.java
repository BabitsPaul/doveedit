package dove.api;

import dove.event.EventRep;
import dove.frame.m2menu.M2Menu;
import dove.frame.m2menu.M2SelectionListener;

import java.awt.*;

public abstract class ComponentApi
        implements M2SelectionListener {
    protected Rectangle size;

    public Rectangle getSize() {
        return size;
    }

    public abstract void moveComponentTo(int dx, int dy);

    public abstract void processEvent(EventRep e);

    public abstract void resizeComponent(Dimension nSize);

    public abstract M2Menu getM2Menu();

    public abstract void paint(Graphics g);
}
