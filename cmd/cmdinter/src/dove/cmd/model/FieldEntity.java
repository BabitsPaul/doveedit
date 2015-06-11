package dove.cmd.model;

import dove.cmd.model.datatypes.Data;

/**
 * Created by Babits on 12/05/2015.
 */
public class FieldEntity
        extends SyntaxEntity {
    private String name;

    private boolean isFinal;

    private Data var;

    private DataType type;

    public FieldEntity(Data data, boolean isFinal, DataType type, String name) {
        this.var = data;
        this.isFinal = isFinal;
        this.type = type;
        this.name = name;
    }

    public FieldEntity(DataType type, String name) {
        this(null, false, type, name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DataType getType() {
        return DataType.FIELD;
    }

    @Override
    public Data getVal() {
        return var;
    }
}