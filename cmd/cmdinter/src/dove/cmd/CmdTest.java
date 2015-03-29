package dove.cmd;

import dove.cmd.ui.CommandLineUI;
import dove.cmd.ui.model.DefaultTextLayerModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CmdTest {
    public static void main(String[] args) {
        CommandLineUI cmd = new CommandLineUI(100, 300);
        ((DefaultTextLayerModel) cmd.getActiveLayer().getModel()).writeln("hello world qwertz WAÖDGKHSI");

        JFrame test = new JFrame();
        test.add(cmd);
        test.setExtendedState(Frame.MAXIMIZED_BOTH);
        test.setExtendedState(JFrame.EXIT_ON_CLOSE);
        test.setVisible(true);
        test.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}