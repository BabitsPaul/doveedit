package dove.cmd.api;

import dove.cmd.interpreter.CommandLineInterpreter;

public abstract class CommandArg {
    public abstract String commandArg();

    public abstract Class[][] listArgTypes();

    public abstract Object performCommand(Object[] args, CommandLineInterpreter model);
}
