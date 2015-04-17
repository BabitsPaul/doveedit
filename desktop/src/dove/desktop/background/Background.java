package dove.desktop.background;

import java.awt.*;

/**
 * Created by Babits on 17/04/2015.
 */
public abstract class Background {
    public abstract void paint(Graphics g, double longitude, double latitude, double zoom, Dimension screen);
}
