package dove.cmd.model.operator;

import dove.cmd.model.DataType;
import dove.cmd.model.MethodEntity;

public abstract class Operator
        extends MethodEntity {
    private String opString;

    public Operator(String opString, boolean isUnary, DataType[] data, DataType output) {
        super("operator" + opString, data, output);
    }

    @Override
    public Object getVal() {
        return this;
    }
}