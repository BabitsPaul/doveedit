package dove.cmd.model.operator;

import dove.cmd.CommandLineData;
import dove.cmd.model.DataType;
import dove.cmd.model.StructureInstanceEntity;
import dove.cmd.model.datatypes.Data;
import dove.cmd.syntax.InputValidate;

public class OpAccStruct
        extends Operator {
    public OpAccStruct() {
        super("->", false, new DataType[]{DataType.INSTANCE, DataType.TEXT}, DataType.HIGH_LEVEL);
    }

    @Override
    public Data invoke(CommandLineData data, Data... input) {
        InputValidate.validateInput(input, getInputTypes());

        return ((StructureInstanceEntity) input[0]).get((String) input[1].getVal());
    }
}