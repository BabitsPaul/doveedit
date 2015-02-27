package dove.frame.m2menu;

import dove.util.treelib.Tree;

import java.awt.*;
import java.util.ArrayList;
import java.util.EventObject;

public class M2Menu {
    private M2Tree options;

    private M2Tree[] selectedPath;

    private ArrayList<M2SelectionListener> listeners;

    private Point startAt = new Point();

    public M2Menu(M2Tree options) {
        this.options = options;
    }

    public void paint(Graphics g) {
        g.setColor(Color.black);

        Rectangle bounds = calcBounds(g.getFontMetrics());

        int at_y = bounds.y;

        for (Tree t : selectedPath[selectedPath.length - 1].listPeers()) {
            g.drawString((String) t.getContent(), bounds.x, at_y);

            at_y += g.getFontMetrics().getHeight() + 5;
        }
    }

    private Rectangle calcBounds(FontMetrics m) {
        int height = m.getHeight();

        Rectangle result = new Rectangle(startAt, new Dimension());

        selectedPath[selectedPath.length - 1].listPeers().forEach((Tree t) -> {
            result.height += height + 5;

            int width = m.stringWidth((String) t.getContent());
            result.width = (result.width < m.stringWidth((String) t.getContent()) ? width : result.width);
        });

        return result;
    }

    public void setPointAt(Point pt) {
        startAt = pt;
    }

    public void addOption(String[] option) {
        options.completePath(option);
    }

    public void process(EventObject e) {

    }

    ///////////////////////////////////////////////////////
    // selectionlistener
    ///////////////////////////////////////////////////////

    public void addM2SelectionListener(M2SelectionListener l) {
        listeners.add(l);
    }

    protected void fireSelection() {
        String[] result = new String[selectedPath.length];
        for (int i = 0; i < result.length; i++) result[i] = selectedPath[i].getContent();

        for (M2SelectionListener l : listeners)
            l.optionSelected(result);
    }
}
