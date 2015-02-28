package dove.util.ui.extensibletable;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.JTableHeader;

public class EditableHeader
        extends JTableHeader
        implements CellEditorListener {
    @Override
    public void editingStopped(ChangeEvent e) {

    }

    @Override
    public void editingCanceled(ChangeEvent e) {

    }
}
