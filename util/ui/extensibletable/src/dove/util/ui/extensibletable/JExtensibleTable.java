package dove.util.ui.extensibletable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.util.ArrayList;

public class JExtensibleTable
        extends JTable {
    private ArrayList<TableSelectionListener> tableSelectionListeners;

    public JExtensibleTable(int rows, int cols) {
        super(new ExtensibleTableModel(rows, cols));

        initAttributes();
    }

    public JExtensibleTable(Object[][] data, String[] name) {
        super(new ExtensibleTableModel(name, null, null, data.length, (data.length == 0 ? 0 : data[0].length)));

        initAttributes();

        ExtensibleTableModel model =
                new ExtensibleTableModel(name, null, null, data.length, (data.length == 0 ? 0 : data[0].length));
        model.insertData(data, 0, 0);
        setModel(model);
    }

    private void initAttributes() {
        tableSelectionListeners = new ArrayList<>();

        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        getTableHeader().setDefaultRenderer(new EditableHeaderCellRenderer());
    }

    ///////////////////////////////////////////////////////////
    // ExtensibleTableModel
    ///////////////////////////////////////////////////////////

    @Override
    public ExtensibleTableModel getModel() {
        return (ExtensibleTableModel) super.getModel();
    }

    public void setModel(ExtensibleTableModel model) {
        super.setModel(model);
    }

    ///////////////////////////////////////////////////////////
    // tableselectionlistener
    ///////////////////////////////////////////////////////////

    @Override
    public void valueChanged(ListSelectionEvent e) {
        //ignore drag and drop
        if (e.getValueIsAdjusting())
            return;

        fireCellSelected();
    }

    public void addTableSelectionListener(TableSelectionListener l) {
        tableSelectionListeners.add(l);
    }

    public void removeTableSelectionListener(TableSelectionListener l) {
        tableSelectionListeners.remove(l);
    }

    protected void fireCellSelected() {
        TableSelectionEvent e = new TableSelectionEvent(this, getSelectedRow(), getSelectedColumn());

        tableSelectionListeners.forEach(l -> l.cellSelected(e));
    }
}