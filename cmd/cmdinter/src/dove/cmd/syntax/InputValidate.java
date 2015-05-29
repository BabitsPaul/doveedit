package dove.cmd.syntax;

import dove.cmd.model.DataType;
import dove.cmd.model.datatypes.Data;

public class InputValidate {
    public static boolean validateInput(Data[] data, DataType[] types) {
        boolean allValid = true;

        if (data == null || types == null)
            return false;

        if (data.length != types.length)
            return false;

        for (int i = 0; i < data.length && allValid; i++)
            allValid = data[i].getType().equals(types[i]);

        return allValid;
    }
}