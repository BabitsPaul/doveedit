package dove.cmd.model.operator;

import dove.cmd.model.DataType;
import dove.cmd.model.datatypes.Data;
import dove.cmd.model.datatypes.Integral;
import dove.cmd.syntax.InputValidate;

public class OpPlusII
        extends Operator {
    public OpPlusII() {
        super("+", false, new DataType[]{DataType.INTEGRAL, DataType.INTEGRAL}, DataType.INTEGRAL);
    }

    @Override
    public Data invoke(Data... input) {
        if (input.length == 2 && InputValidate.validateInput(input, getInputTypes()))
            return new Integral(((Integral) input[0]).getVal() + ((Integral) input[1]).getVal());
        else
            return null;
    }
}
