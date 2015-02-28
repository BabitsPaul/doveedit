package dove.util.ui.extensibletable;

@FunctionalInterface
public interface TableSelectionListener {
    public void cellSelected(TableSelectionEvent e);
}