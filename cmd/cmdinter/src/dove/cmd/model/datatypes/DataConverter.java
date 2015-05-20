package dove.cmd.model.datatypes;

import dove.cmd.model.DataType;

public class DataConverter {
    public static Data convert(Data d, DataType to)
            throws ConversionException {
        switch (d.getType()) {
            case INTEGRAL:
                switch (to) {
                    case INTEGRAL:
                        return new Integral(((Integral) d).getVal());

                    case TEXT:
                        return new Text(d.getVal().toString());

                    case FLOAT:
                        return new Float(((Integral) d).getVal());

                    case BOOL:
                        return new Bool(((Integral) d).getVal() != 0);
                }
                break;

            case TEXT:
                switch (to) {
                    case INTEGRAL:
                        try {
                            return new Integral(Long.parseLong(d.getVal().toString()));
                        } catch (NumberFormatException e) {
                            throw new ConversionException(d.getVal().toString() + " is no valid representation of an integer");
                        }

                    case FLOAT:
                        try {
                            return new Float(Double.parseDouble(d.getVal().toString()));
                        } catch (NumberFormatException e) {
                            throw new ConversionException(d.getVal().toString() + " is no valid representation of a floatingpoint number");
                        }

                    case BOOL:
                        String s = d.getVal().toString().trim();

                        if (s.length() == 0)
                            throw new ConversionException("Invalid input - can't convert EMPTY to BOOL");

                        if (s.length() == 1)
                            try {
                                return new Bool(Integer.parseInt(s) != 0);
                            } catch (NumberFormatException e) {
                                throw new ConversionException("Invalid input - can't convert " + s + " to BOOL");
                            }
                        else
                            return new Bool(Boolean.parseBoolean(s));
                }
                break;

            case FLOAT:
                switch (to) {
                    case INTEGRAL:
                        return new Integral(Math.round(((Float) d).getVal()));

                    case TEXT:
                        return new Text(d.getVal().toString());

                    case BOOL:
                        return new Bool(((Float) d).getVal() != 0);

                    case FLOAT:
                        return new Float(((Float) d).getVal());
                }
                break;

            case BOOL:
                switch (to) {
                    case INTEGRAL:
                        return new Integral(((Bool) d).getVal() ? 1 : 0);

                    case FLOAT:
                        return new Float(((Bool) d).getVal() ? 1 : 0);

                    case TEXT:
                        return new Text(String.valueOf(d.getVal()));

                    case BOOL:
                        return new Bool((boolean) d.getVal());
                }
                break;
        }

        throw new ConversionException("Invalid input can't convert [" + d.getType() + "]" + d.getVal() + " to " + to);
    }

    public static Data generateData(Object input)
            throws ConversionException {
        try {
            if (input instanceof Integer || input instanceof Long || input instanceof Short) {
                return new Integral((long) input);
            }

            if (input instanceof String) {
                return new Text((String) input);
            }

            if (input instanceof Boolean) {
                return new Bool((Boolean) input);
            }

            if (input instanceof java.lang.Float || input instanceof Double) {
                return new Float((double) input);
            }
        } catch (ClassCastException e) {
            throw new ConversionException("Failed to create data - cause: " + e.getMessage());
        }

        throw new ConversionException("Can't convert " + input.getClass().getCanonicalName() + " to any type");
    }

    public static class ConversionException
            extends Exception {
        public ConversionException(String msg) {
            super(msg);
        }
    }
}
