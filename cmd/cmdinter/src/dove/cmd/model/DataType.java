package dove.cmd.model;

public enum DataType {
    TEXT,
    INTEGRAL,
    FLOAT,
    BOOLEAN,
    STRUCTURE,
    METHOD,
    FIELD;

    public boolean isValid(byte[] bytes) {
        return false;
    }
}
