package dove.cmd;

import dove.cmd.interpreter.CommandLineInterpreter;
import dove.cmd.interpreter.CommandLineLayerModel;
import dove.cmd.ui.CommandLineUI;

import javax.swing.*;

public class CommandLine {
    private CommandLineUI ui;

    private CommandLineInterpreter interpreter;

    private CommandLineConfiguration config;

    private CmdOpIF cmdOpIF;

    public CommandLine() {
        config = new CommandLineConfiguration();

        initConfiguration();
        ui = new CommandLineUI((int) config.get("dove.cmd.width"),
                (Integer) config.get("dove.cmd.height"), (int) config.get("dove.cmd.cursorfreq"));

        cmdOpIF = new CmdOpIF();

        interpreter = new CommandLineInterpreter(cmdOpIF);

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
        config.put("dove.cmd.cursorfreq", 500);
    }
}