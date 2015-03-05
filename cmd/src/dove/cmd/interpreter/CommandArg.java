package dove.cmd.interpreter;

public abstract class CommandArg {
    public abstract String commandArg();

    public abstract Class[][] listArgTypes();

    public abstract Object performCommand(Object[] args, CommandLineModel model);
}
