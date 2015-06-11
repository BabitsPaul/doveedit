package dove.cmd.model.datatypes;

import dove.cmd.CommandLineData;
import dove.cmd.model.DataType;

public abstract class Data {
    public Data(CommandLineData data) {

    }

    public abstract DataType getType();

    public abstract Object getVal();
}
