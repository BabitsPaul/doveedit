package dove.util.ui.extensibletable;

@FunctionalInterface
public interface CellSelectionListener {
    public void cellSelected(CellSelectionEvent e);
}