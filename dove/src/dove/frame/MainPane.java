package dove.frame;

import dove.Setup;
import dove.api.ComponentApi;
import dove.document.DocumentContext;
import dove.event.EventRep;
import dove.util.collections.SortedList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class MainPane
        extends JPanel {
    private ArrayList<ComponentApi> components;

    private DocumentContext document;

    private RenderHandle render;
    private ComponentApi activeComponent = null;

    public MainPane(DocumentContext context) {
        document = context;

        components = new SortedList<>((a, b) -> (a.getSize().y < b.getSize().y ||
                a.getSize().x < b.getSize().x ? 1 : -1));

        render = new RenderHandle(components);
    }

    public void add(ComponentApi component) {
        //render holds ref to components -> is automatically updated
        components.add(component);

        document.idGiver.giveID(component);
    }

    public void remove(ComponentApi component) {
        components.remove(component);

        document.idGiver.releaseID(component);
    }

    @Override
    protected void paintComponent(Graphics gr) {
        //TODO transfer to RenderHandle
        super.paintComponent(gr);

        Graphics2D g = (Graphics2D) gr;

        render.paint(g);

        //TODO bounds
        if (activeComponent != null) {
            g.setColor(Color.green);
            g.setStroke(new BasicStroke(4.0f));
            Rectangle r = activeComponent.getSize();
            g.drawRect(r.x - 2, r.y - 2, r.width + 2, r.height + 2);
        }
    }

    public void process(EventRep e) {
        if (e.get("type").equals("java.awt.event.WindowEvent")) {
            if ((int) e.get("id") == WindowEvent.WINDOW_CLOSED) {
                Setup.tearDown(document);
            }
        }

        if (isOnActiveComponent(e) && activeComponent != null) {
            if (e.get("type").equals("java.awt.event.MouseEvent")
                    && (int) e.get("button") == MouseEvent.BUTTON2) {
                render.displayM2Menu(activeComponent.getM2Menu());
            }
            else {
                if (render.m2menuDisplayed())
                    render.hideM2Menu();
            }

            activeComponent.processEvent(e);
        }
        else
            repaint();
    }

    private boolean isOnActiveComponent(EventRep o) {
        if (o.get("type").equals("java.awt.event.MouseEvent") &&
                (int) o.get("id") == MouseEvent.MOUSE_CLICKED) {

            for (ComponentApi api : components) {
                if (api.getSize().contains((Point) o.get("point"))) {
                    if (api.equals(activeComponent))
                        return true;
                    else {
                        activeComponent = api;

                        return true;
                    }
                }
            }

            activeComponent = null;

            return false;
        }
        else
            return true;
    }
}