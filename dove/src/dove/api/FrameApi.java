package dove.api;

import dove.event.EventListener;
import dove.event.EventRep;

import javax.swing.*;
import java.awt.*;

public interface FrameApi {
    public void add(ComponentApi component);

    public void remove(ComponentApi component);

    public void process(EventRep e);

    public void setupListener(EventListener listener);

    public void documentChanged();

    public Dimension getSize();

    public Component getComponent();

    public JMenuBar getJMenubar();

    public void update(Runnable toUpdate);
}