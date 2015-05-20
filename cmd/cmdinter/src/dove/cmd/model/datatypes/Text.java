package dove.cmd.model.datatypes;

import dove.cmd.model.DataType;

public class Text
        extends Data {
    private String val;

    public Text(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    @Override
    public DataType getType() {
        return DataType.TEXT;
    }
}
