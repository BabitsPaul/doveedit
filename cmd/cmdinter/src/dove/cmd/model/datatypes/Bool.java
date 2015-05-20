package dove.cmd.model.datatypes;

import dove.cmd.model.DataType;

/**
 * Created by Babits on 20/05/2015.
 */
public class Bool
        extends Data {
    private boolean val;

    public Bool(boolean val) {
        this.val = val;
    }

    public boolean getVal() {
        return val;
    }

    public DataType getType() {
        return DataType.BOOL;
    }
}
