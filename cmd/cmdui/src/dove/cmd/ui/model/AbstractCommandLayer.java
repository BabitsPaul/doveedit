package dove.cmd.ui.model;

import dove.cmd.ui.paint.AbstractLayerRenderer;
import dove.util.keyredirect.KeyRedirect;

import java.awt.event.KeyListener;
import java.util.ArrayList;

public abstract class AbstractCommandLayer
        extends KeyRedirect {
    public static final char NO_CHAR = '\u0000';

    private ArrayList<CommandLineUIListener> listeners = new ArrayList<>();

    private Cursor cursor;

    private CharBuffer buffer;

    public AbstractCommandLayer(KeyListener redirectEvents, Cursor cursor, CharBuffer buffer) {
        redirectTo(redirectEvents);
        this.cursor = cursor;
        this.buffer = buffer;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public CharBuffer getBuffer() {
        return buffer;
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

    public abstract AbstractLayerRenderer createRenderer();

    public abstract Object getModel();
}
