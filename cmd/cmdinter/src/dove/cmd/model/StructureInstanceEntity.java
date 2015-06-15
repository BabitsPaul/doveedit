package dove.cmd.model;

import dove.cmd.CommandLineData;
import dove.cmd.model.datatypes.Data;

import java.util.Map;

public class StructureInstanceEntity
        extends Data {
    private Map<String, Data> nameToData;

    public StructureInstanceEntity(CommandLineData data, StructureEntity structure) {
        super();
    }

    public Data get(String name) {
        if (!nameToData.containsKey(name))
            throw new IllegalArgumentException("Invalid attribute name: " + name);

        return nameToData.get(name);
    }

    @Override
    public DataType getType() {
        return DataType.INSTANCE;
    }

    @Override
    public Object getVal() {
        return this;
    }
}