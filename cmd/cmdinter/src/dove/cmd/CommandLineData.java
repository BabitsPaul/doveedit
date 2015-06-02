package dove.cmd;

import dove.cmd.model.FieldEntity;
import dove.cmd.model.SyntaxEntity;
import dove.cmd.model.operator.Operator;

import java.util.Map;

public class CommandLineData {
    public Map<String, Operator> operators;
    public Map<String, SyntaxEntity> entities;
    public Map<String, FieldEntity> fields;
}
