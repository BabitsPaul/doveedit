package dove.cmd.model.operator;

import dove.cmd.CommandLineData;
import dove.cmd.model.DataType;
import dove.cmd.model.FieldEntity;
import dove.cmd.model.datatypes.Data;
import dove.cmd.syntax.InputValidate;

public class OpAccess
        extends Operator {
    public OpAccess() {
        super("$", true, new DataType[]{DataType.FIELD}, DataType.ANY);
    }

    @Override
    public Data invoke(CommandLineData data, Data... input) {
        InputValidate.validateInput(input, getInputTypes());

        return ((FieldEntity) input[0]).getData();
    }

    @Override
    public Object getVal() {
        return this;
    }
}