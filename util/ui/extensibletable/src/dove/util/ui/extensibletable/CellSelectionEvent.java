package dove.util.ui.extensibletable;

import javax.swing.*;
import java.util.EventObject;

public class CellSelectionEvent
        extends EventObject {
    private int row;
    private int col;

    public CellSelectionEvent(JTable src, int row, int col) {
        super(src);

        this.row = row;
        this.col = col;
    }

    public JTable getSource() {
        return (JTable) super.getSource();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
