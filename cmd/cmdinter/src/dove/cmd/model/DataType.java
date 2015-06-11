package dove.cmd.model;

import java.util.Arrays;

public enum DataType {
    TEXT(true),
    INTEGRAL(true),
    FLOAT(true),
    BOOL(true),
    STRUCTURE(false),
    METHOD(false),
    FIELD(false),
    VOID(true),
    INSTANCE(false),
    ARRAY(true),
    HIGH_LEVEL(false, STRUCTURE, METHOD, FIELD, INSTANCE),
    ANY(false, TEXT, INTEGRAL, FLOAT, BOOL, STRUCTURE, METHOD, FIELD, INSTANCE, ARRAY);

    private DataType[] substitutes;

    private boolean isPrimitive;

    DataType(boolean isPrimitive, DataType... substitutes) {
        this.substitutes = new DataType[substitutes.length + 1];
        System.arraycopy(substitutes, 0, this.substitutes, 0, substitutes.length);
        this.substitutes[substitutes.length] = this;

        this.isPrimitive = isPrimitive;
    }

    public boolean isPrimitive() {
        return isPrimitive;
    }

    public boolean validSubstitute(DataType type) {
        return Arrays.stream(substitutes).anyMatch(s -> type.equals(s));
    }
}
