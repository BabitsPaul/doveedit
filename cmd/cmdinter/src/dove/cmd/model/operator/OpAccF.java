package dove.cmd.model.operator;

import dove.cmd.CommandLineData;
import dove.cmd.model.DataType;
import dove.cmd.model.FieldEntity;
import dove.cmd.model.datatypes.Data;
import dove.cmd.syntax.InputValidate;

/**
 * Operator for accessing the data held by a given field
 */
public class OpAccF
        extends Operator {
    public OpAccF() {
        super("$", true, new DataType[]{DataType.FIELD}, DataType.ANY);
    }

    @Override
    public Data invoke(CommandLineData data, Data... input) {
        InputValidate.validateInput(input, getInputTypes());

        return ((FieldEntity) input[0]).getVal();
    }
}