package dove.cmd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CmdTest {
    public static void main(String[] args) {
        CommandLineUI cmd = new CommandLineUI();

        cmd.getModel().put("commandline.color.foreground", Color.BLACK);
        cmd.getModel().put("commandline.color.background", Color.WHITE);

        JFrame test = new JFrame("Commandline");
        test.setContentPane(cmd);
        test.setExtendedState(Frame.MAXIMIZED_BOTH);
        test.setExtendedState(JFrame.EXIT_ON_CLOSE);
        test.setVisible(true);

        cmd.write("hello world");

        test.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
