package dove.cmd.model;

import java.util.Arrays;

public enum DataType {
    TEXT,
    INTEGRAL,
    FLOAT,
    BOOL,
    STRUCTURE,
    METHOD,
    FIELD,
    VOID,
    INSTANCE,
    ARRAY,
    ANY(TEXT, INTEGRAL, FLOAT, BOOL, STRUCTURE, METHOD, FIELD, INSTANCE, ARRAY);

    private DataType[] substitutes;

    DataType(DataType... substitutes) {
        this.substitutes = new DataType[substitutes.length + 1];
        System.arraycopy(substitutes, 0, this.substitutes, 0, substitutes.length);
        this.substitutes[substitutes.length] = this;
    }

    public boolean validSubstitute(DataType type) {
        return Arrays.stream(substitutes).anyMatch(s -> type.equals(s));
    }
}
