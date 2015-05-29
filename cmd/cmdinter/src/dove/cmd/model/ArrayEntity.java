package dove.cmd.model;

import dove.cmd.model.datatypes.Data;

public class ArrayEntity
        extends Data {
    private Data[] data;
    private DataType type;

    public ArrayEntity(int space, DataType type) {
        this.data = new Data[space];
    }

    public ArrayEntity(Data[] data, DataType type) {
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