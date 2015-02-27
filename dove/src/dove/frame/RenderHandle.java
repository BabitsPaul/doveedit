package dove.frame;

import dove.api.ComponentApi;
import dove.frame.m2menu.M2Menu;

import java.awt.*;
import java.util.ArrayList;

public class RenderHandle {
    private M2Menu m2Menu;
    private ArrayList<ComponentApi> toRender = new ArrayList<>();

    public RenderHandle(ArrayList<ComponentApi> toRender) {
        this.toRender = toRender;
    }

    public void paint(Graphics2D g) {
        toRender.forEach(r -> {
            Rectangle rSize = r.getSize();
            g.hitClip(rSize.x, rSize.y, rSize.width, rSize.height);
            r.paint(g);
        });

        if (m2Menu != null)
            m2Menu.paint(g);
    }

    public void displayM2Menu(M2Menu toDisplay) {
        m2Menu = toDisplay;
    }

    public void hideM2Menu() {
        m2Menu = null;
    }

    public boolean m2menuDisplayed() {
        return (m2Menu != null);
    }
}