package dove.cmd.syntax;

import dove.cmd.model.DataType;
import dove.cmd.model.datatypes.Data;

public class InputValidate {
    public static void validateInput(Data[] data, DataType[] types) {
        boolean allValid = true;

        if (data == null || types == null)
            throw new IllegalArgumentException("Invalid input: null");

        if (data.length != types.length)
            throw new IllegalArgumentException("Invalid input: input doesn't match required length");

        for (int i = 0; i < data.length && allValid; i++)
            if (!types[i].validSubstitute(data[i].getType()))
                throw new IllegalArgumentException("Invalid input: parameter #" + i + " doesn't match " + types[i]);
    }
}