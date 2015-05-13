import javax.swing.*;

public class SO {

    public static void main(String[] args) {

        JLabel label = new JLabel("This is a label");

        JPanel panel = new JPanel();

        JFrame frame = new JFrame("Window Title");
        frame.setSize(800, 500);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(panel);
        panel.add(label);

        frame.setVisible(true);
    }

}