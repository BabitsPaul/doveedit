package dove.cmd.ui.model;

import java.util.ArrayList;

public abstract class AbstractLayerModel {
    private ArrayList<CommandLineUIListener> listeners;

    public AbstractLayerModel() {
        listeners = new ArrayList<>();
    }

    public void addCommandLineUIListener(CommandLineUIListener l) {
        listeners.add(l);
    }

    public void removeCommandLineUILIstener(CommandLineUIListener l) {
        listeners.remove(l);
    }

    protected void fireLayerModelChanged(CommandLineEvent e) {
        listeners.forEach(l -> l.commandLineChanged(e));
    }
}
