package dove.cmd.model;

import dove.cmd.model.datatypes.Data;

public class StructureEntity
        extends Data {
    private String name;

    private DataType[] fields;

    private MethodEntity[] methods;

    public StructureEntity(String name, DataType[] fields, MethodEntity[] methods) {
        this.name = name;
        this.fields = fields;
        this.methods = methods;
    }

    public String getName() {
        return name;
    }

    public DataType[] getFields() {
        return fields;
    }

    public MethodEntity[] getMethods() {
        return methods;
    }

    @Override
    public DataType getType() {
        return DataType.STRUCTURE;
    }

    @Override
    public Object getVal() {
        return this;
    }
}
