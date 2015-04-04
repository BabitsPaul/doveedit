package dove.cmd;

import dove.cmd.interpreter.CommandLineInterpreter;
import dove.cmd.interpreter.CommandLineLayerModel;
import dove.cmd.ui.CommandLineUI;

import javax.swing.*;

public class CommandLine
        extends JPanel {
    private CommandLineUI ui;

    private CommandLineInterpreter interpreter;

    private CommandLineConfiguration config;

    public CommandLine() {
        config = new CommandLineConfiguration();

        ui = new CommandLineUI((Integer) config.get("dove.cmd.width"), (Integer) config.get("dove.cmd.height"));

        interpreter = new CommandLineInterpreter();

        ui.setModel(new CommandLineLayerModel(ui.getBuffer(), ui.getCmdCursor(), ui.getClip(), interpreter));
    }

    //TODO later replaced with data from .cfg file
    private void initConfiguration() {
        config.put("dove.cmd.width", 50);
        config.put("dove.cmd.height", 200);
    }
}