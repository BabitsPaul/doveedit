package dove.cmd.model.datatypes;

import dove.cmd.model.DataType;

public class DataConverter {
    public static Data convert(Data d, DataType to)
            throws ConversionException {
        switch (d.getType()) {
            case INTEGRAL:
                break;

            case TEXT:
                break;

            case FLOAT:
                break;

            case BOOL:
                break;
        }

        throw new ConversionException("Invalid type can't convert " + d.getType() + " to " + to);
    }

    public static class ConversionException
            extends Exception {
        public ConversionException(String msg) {
            super(msg);
        }
    }
}
