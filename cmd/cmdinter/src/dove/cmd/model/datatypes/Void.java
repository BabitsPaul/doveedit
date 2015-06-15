package dove.cmd.model.datatypes;

import dove.cmd.model.DataType;

public class Void
        extends Data {
    public static final Void VOID = new Void();

    private Void() {
        super();
    }

    @Override
    public DataType getType() {
        return DataType.VOID;
    }

    @Override
    public Object getVal() {
        return null;
    }
}
