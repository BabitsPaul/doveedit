package dove.cmd.ui.model;

import java.util.ArrayList;

public abstract class CommandLineElement {
    private ArrayList<CommandLineUIListener> listeners;

    public CommandLineElement() {
        listeners = new ArrayList<>();
    }

    public void addCommandLineListener(CommandLineUIListener l) {
        listeners.add(l);
    }

    public void removeCommandLineListener(CommandLineUIListener l) {
        listeners.remove(l);
    }

    protected void fireCommandLineEvent(CommandLineEvent e) {
        listeners.forEach(l -> l.commandLineChanged(e));
    }
}
