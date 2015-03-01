package dove.util.ui.extensibletable;

import javax.swing.*;

public class EditableTableTest {
    public static void main(String[] args) {
        Object[][] data = new Object[][]
                {
                        {
                                "A", "B", "C"
                        },
                        {
                                "1", "2", "3"
                        }
                };

        String[] header = new String[]{
                "a", "b", "c"
        };

        JTable table = new JTable(data, header);

        JFrame frame = new JFrame("Test");
        frame.add(table);
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}