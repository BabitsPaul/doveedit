package dove.cmd.model.operator;

import dove.cmd.CommandLineData;
import dove.cmd.model.DataType;
import dove.cmd.model.datatypes.Data;
import dove.cmd.model.datatypes.Integral;
import dove.cmd.syntax.InputValidate;

/**
 * addition operator for integer values
 */
public class OpPlusII
        extends Operator {
    public OpPlusII() {
        super("+", false, new DataType[]{DataType.INTEGRAL, DataType.INTEGRAL}, DataType.INTEGRAL);
    }

    @Override
    public Data invoke(CommandLineData data, Data... input) {
        InputValidate.validateInput(input, getInputTypes());

        return new Integral(((Integral) input[0]).getVal() + ((Integral) input[1]).getVal());
    }


}
