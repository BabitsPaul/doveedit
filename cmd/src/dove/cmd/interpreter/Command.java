package dove.cmd.interpreter;

public interface Command {
    public String getCommandName();

    public String getShortDescription();

    public String getLongDescription();

    public default void execute(CommandArg[] arg, Object[][] args, CommandLineInterpreter model) {
        for (int i = 0; i < arg.length; i++) {
            CommandArg arg1 = arg[i];
            Object[] args1 = args[i];

            arg1.performCommand(args1, model);
        }
    }

    public CommandArg[] listArgs();

    public void firstSetup(CommandLineInterpreter model);
}