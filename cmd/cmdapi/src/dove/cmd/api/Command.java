package dove.cmd.api;

import dove.cmd.interpreter.CommandLineInterpreter;

public interface Command {
    String getCommandName();

    String getShortDescription();

    String getLongDescription();

    default void execute(CommandArg[] arg, Object[][] args, CommandLineInterpreter model) {
        for (int i = 0; i < arg.length; i++) {
            CommandArg arg1 = arg[i];
            Object[] args1 = args[i];

            arg1.performCommand(args1, model);
        }
    }

    CommandArg[] listArgs();

    void firstSetup(CommandLineInterpreter model);
}