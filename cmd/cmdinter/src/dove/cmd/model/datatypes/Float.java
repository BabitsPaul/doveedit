package dove.cmd.model.datatypes;

import dove.cmd.model.DataType;

/**
 * Created by Babits on 20/05/2015.
 */
public class Float
        extends Data {
    private double val;

    public Float(double val) {
        this.val = val;
    }

    public double getVal() {
        return val;
    }

    public DataType getType() {
        return DataType.FLOAT;
    }
}
