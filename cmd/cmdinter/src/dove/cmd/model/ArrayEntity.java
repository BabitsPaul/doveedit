package dove.cmd.model;

import dove.cmd.CommandLineData;
import dove.cmd.model.datatypes.Data;

public class ArrayEntity
        extends Data {
    private Data[] data;
    private DataType type;

    public ArrayEntity(int space, DataType type, CommandLineData data) {
        super();

        this.data = new Data[space];
    }

    public ArrayEntity(Data[] data, DataType type, CommandLineData cmddata) {
        super();

        this.data = new Data[data.length];
        System.arraycopy(data, 0, data, 0, data.length);

        this.type = type;
    }

    @Override
    public DataType getType() {
        return DataType.ARRAY;
    }

    @Override
    public Data[] getVal() {
        return data;
    }
}