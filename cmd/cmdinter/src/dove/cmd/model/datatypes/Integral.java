package dove.cmd.model.datatypes;

import dove.cmd.model.DataType;

/**
 * Created by Babits on 20/05/2015.
 */
public class Integral {
    private long val;

    public Integral(long val) {
        this.val = val;
    }

    public static DataType getType() {
        return DataType.INTEGRAL;
    }
}
