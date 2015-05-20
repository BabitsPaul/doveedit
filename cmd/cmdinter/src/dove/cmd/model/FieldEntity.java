package dove.cmd.model;

/**
 * Created by Babits on 12/05/2015.
 */
public class FieldEntity
        extends CmdEntity {
    private String name;
    private Object var;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DataType getType() {
        return null;
    }
}
