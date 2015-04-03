package dove.cmd.systemcall;

import dove.cmd.api.Command;
import dove.cmd.api.CommandArg;
import dove.cmd.interpreter.CommandLineInterpreter;

import java.io.IOException;

public class SystemCall
        implements Command {
    private static final String SHORT_DESCRIPTION = "Calls the OS Commandline with the given command";

    private static final String LONG_DESCRIPTION =
            "Calls the OS commandline and starts it with the\n" +
                    "given command";

    @Override
    public String getCommandName() {
        return "systemcall";
    }

    @Override
    public String getShortDescription() {
        return SHORT_DESCRIPTION;
    }

    @Override
    public String getLongDescription() {
        return LONG_DESCRIPTION;
    }

    @Override
    public CommandArg[] listArgs() {
        return new CommandArg[]{
                new CommandArg() {
                    @Override
                    public String commandArg() {
                        return null;
                    }

                    @Override
                    public Class[][] listArgTypes() {
                        return new Class[][]{{String.class}};
                    }

                    @Override
                    public Object performCommand(Object[] args, CommandLineInterpreter model) {
                        try {
                            Process p = Runtime.getRuntime().exec((String) args[0]);

                            byte[] buffer = new byte[1024];
                            int byteCount;
                            while ((byteCount = p.getInputStream().read(buffer)) != 0)
                                System.out.println(new String(buffer, 0, byteCount));
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                }
        };
    }

    @Override
    public void firstSetup(CommandLineInterpreter model) {

    }
}