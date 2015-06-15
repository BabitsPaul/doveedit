package dove.cmd.model.operator;

public class OperatorStub {
    public boolean isUnary;
    public String operator;
    public boolean isPrefix;

    public OperatorStub(String operator, boolean isUnary, boolean isPrefix) {
        this.operator = operator;
        this.isUnary = isUnary;
        this.isPrefix = isPrefix;
    }
}