package dove.cmd.model;

import dove.cmd.CommandLineData;
import dove.cmd.model.datatypes.Data;

/**
 * Created by Babits on 12/05/2015.
 */
public class MethodEntity
        extends SyntaxEntity {
    private String name;

    private DataType[] input;

    private DataType output;

    public MethodEntity(String name, DataType[] input, DataType output) {
        this.name = name;
        this.input = input;
        this.output = output;
    }

    public Data invoke(CommandLineData data, Data... input) {


        return null;
    }

    public DataType[] getInputTypes() {
        return input;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DataType getType() {
        return DataType.METHOD;
    }
}
