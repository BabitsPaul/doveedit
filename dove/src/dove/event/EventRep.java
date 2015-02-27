package dove.event;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class EventRep {
    private HashMap<String, Object> properties;

    public EventRep(InputEvent o) {
        properties = new HashMap<>();

        properties.put("type", o.getClass().getName());
        properties.put("id", o.getID());
        properties.put("modifier", o.getModifiers());

        if (o instanceof MouseEvent) {
            MouseEvent m = (MouseEvent) o;

            properties.put("button", m.getButton());
            properties.put("clickcount", m.getClickCount());
            properties.put("point", m.getPoint());
        }
        else if (o instanceof MouseWheelEvent) {
            MouseWheelEvent w = (MouseWheelEvent) o;
            properties.put("wheelrotation", w.getPreciseWheelRotation());
            properties.put("scrollamout", w.getScrollAmount());
            properties.put("scrolltype", w.getScrollType());
        }
        else if (o instanceof KeyEvent) {
            KeyEvent k = (KeyEvent) o;
            properties.put("keycode", k.getKeyCode());
            properties.put("when", k.getWhen());
        }
    }

    public EventRep(AWTEvent e) {
        properties = new HashMap<>();

        properties.put("type", e.getClass().getName());
        properties.put("id", e.getID());

        if (e instanceof ComponentEvent) {
            ComponentEvent c = (ComponentEvent) e;

            properties.put("component", c.getComponent());
        }
    }

    public Object get(String key) {
        return properties.get(key);
    }
}
