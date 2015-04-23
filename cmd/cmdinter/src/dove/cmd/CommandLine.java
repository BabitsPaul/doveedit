package dove.cmd;

import dove.cmd.interpreter.CommandLineInterpreter;
import dove.cmd.interpreter.CommandLineLayerModel;
import dove.cmd.ui.CommandLineUI;
import dove.cmd.ui.model.PositionHelper;

import javax.swing.*;

public class CommandLine {
    private CommandLineUI ui;

    private CommandLineInterpreter interpreter;

    private CommandLineConfiguration config;

    public CommandLine() {
        config = new CommandLineConfiguration();

        initConfiguration();
        ui = new CommandLineUI((Integer) config.get("dove.cmd.width"), (Integer) config.get("dove.cmd.height"));
        ui.getCmdCursor().setPosition(new PositionHelper.Position(0, 0, false));
        ui.getActiveLayer().getBuffer().put('Q');

        interpreter = new CommandLineInterpreter();

        ui.setModel(new CommandLineLayerModel(ui.getBuffer(), ui.getCmdCursor(), ui.getClip(), interpreter));
    }

    public static void createWindow() {
        CommandLine cmd = new CommandLine();

        //JScrollPane jsp = new JScrollPane(cmd.ui);
        //jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        JFrame frame = new JFrame("Commandline");
        frame.setLayout(null);
        frame.setContentPane(cmd.ui);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    //TODO later replaced with data from .cfg file
    private void initConfiguration() {
        config.put("dove.cmd.width", 50);
        config.put("dove.cmd.height", 200);
    }
}