package dove.util.treelib;

import java.util.EventObject;

public class TreeModelChangedEvent
        extends EventObject {
    private TYPE type;

    public TreeModelChangedEvent(Tree source, TYPE type) {
        super(source);

        this.type = type;
    }

    @Override
    public Tree getSource() {
        return (Tree) source;
    }

    public TYPE getType() {
        return type;
    }

    enum TYPE {
        ADD,
        REMOVE,
        VALUE_UPDATED,
        CLEAR,
        MERGE
    }
}
