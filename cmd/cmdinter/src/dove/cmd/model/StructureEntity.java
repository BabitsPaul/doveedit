package dove.cmd.model;

import dove.cmd.model.datatypes.Data;

/**
 * Created by Babits on 12/05/2015.
 */
public class StructureEntity
        extends Data {


    @Override
    public DataType getType() {
        return DataType.STRUCTURE;
    }

    @Override
    public Object getVal() {
        return null;
    }
}
