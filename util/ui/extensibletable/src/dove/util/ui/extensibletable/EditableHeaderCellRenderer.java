package dove.util.ui.extensibletable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class EditableHeaderCellRenderer
        extends JTextField
        implements TableCellRenderer {
    public EditableHeaderCellRenderer() {
        setEnabled(true);
        setEditable(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setEditable(true);
        setText(value.toString());

        JTable.DropLocation dropLocation = table.getDropLocation();
        if (dropLocation != null
                && !dropLocation.isInsertRow()
                && !dropLocation.isInsertColumn()
                && dropLocation.getRow() == row
                && dropLocation.getColumn() == column) {
            // this cell represents the current drop location
            // so render it specially, perhaps with a different color
        }

        return this;
    }
}
