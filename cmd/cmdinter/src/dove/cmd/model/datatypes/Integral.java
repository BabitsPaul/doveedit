package dove.cmd.model.datatypes;

import dove.cmd.model.DataType;

/**
 * Created by Babits on 20/05/2015.
 */
public class Integral
        extends Data {
    private long val;

    public Integral(long val) {
        this.val = val;
    }

    public Long getVal() {
        return val;
    }

    public DataType getType() {
        return DataType.INTEGRAL;
    }
}
