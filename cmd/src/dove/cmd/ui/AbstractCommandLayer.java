package dove.cmd.ui;

import dove.util.keyredirect.KeyRedirect;

import java.awt.event.KeyListener;
import java.util.ArrayList;

public abstract class AbstractCommandLayer
        extends KeyRedirect {
    public static final char NO_CHAR = '\u0000';

    private ArrayList<CommandLineUIListener> listeners = new ArrayList<>();

    public AbstractCommandLayer(KeyListener redirectEvents) {
        redirectTo(redirectEvents);
    }

    public void addListener(CommandLineUIListener l) {
        listeners.add(l);
    }

    public void removeListener(CommandLineUIListener l) {
        listeners.remove(l);
    }

    protected void fireCommandLayerEvent(CommandLineEvent e) {
        listeners.forEach(l -> l.commandLineChanged(e));
    }

    public abstract void enableLayer();
}
