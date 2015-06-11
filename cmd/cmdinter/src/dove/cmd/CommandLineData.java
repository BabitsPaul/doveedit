package dove.cmd;

import dove.cmd.model.FieldEntity;
import dove.cmd.model.SyntaxEntity;
import dove.cmd.model.datatypes.Data;
import dove.cmd.model.operator.Operator;

import java.util.ArrayList;
import java.util.Map;

public class CommandLineData {
    public Map<String, Operator> operators;
    public Map<String, SyntaxEntity> entities;
    public Map<String, FieldEntity> fields;
    private long nextID;
    private ArrayList<Long> freeIDs;

    public CommandLineData() {
        this.nextID = 0L;
        this.freeIDs = new ArrayList<>();
    }

    public void createID(Data data) {

    }
}
