package dove.cmd.model.operator;

import dove.cmd.model.DataType;
import dove.cmd.model.MethodEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Operator
        extends MethodEntity {
    public static Map<String, Integer> PRIORITY_MAP;

    static {
        HashMap<String, Integer> pm = new HashMap<>();
        PRIORITY_MAP.put("&", 0);//TODO all default operators and priority
        PRIORITY_MAP = Collections.unmodifiableMap(pm);
    }

    private String opString;

    public Operator(String opString, boolean isUnary, DataType[] data, DataType output) {
        super("operator" + opString, data, output);
    }

    @Override
    public Object getVal() {
        return this;
    }
}