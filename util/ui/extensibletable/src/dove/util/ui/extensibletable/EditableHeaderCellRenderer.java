package dove.util.ui.extensibletable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class EditableHeaderCellRenderer
        extends JTextField
        implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setEditable(true);
        setText(value.toString());

        return this;
    }
}
