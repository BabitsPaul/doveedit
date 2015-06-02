package dove.cmd.model;

import dove.cmd.model.datatypes.Data;

/**
 * Created by Babits on 12/05/2015.
 */
public class FieldEntity
        extends SyntaxEntity {
    private String name;
    private Data var;

    public Data getData() {
        return var;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DataType getType() {
        return null;
    }
}