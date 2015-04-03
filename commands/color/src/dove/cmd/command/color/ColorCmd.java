package dove.cmd.command.color;

import dove.cmd.api.Command;
import dove.cmd.api.CommandArg;
import dove.cmd.api.CommandError;
import dove.cmd.interpreter.CommandLineInterpreter;

import java.awt.*;

public class ColorCmd
        implements Command {

    private static final String COMMAND_NAME = "color";

    private static final String SHORT_DESCRIPTION = "creates a color";

    private static final String LONG_DESCRIPTION =
            SHORT_DESCRIPTION + "\n" +
                    "Colors can be created in one of the following ways:\n" +
                    "either one of the following predefined: \n" +
                    "yellow, red, green, blue, black, white \n" +
                    "or defined by an rgb-value - either hex or dec\n" +
                    "examples: \n" +
                    "color white - creates a new representation of the color white\n" +
                    "color 0xFF 0x00 0xFF - creates a purple color\n" +
                    "color 255 0 255 - createse a purple color\n";

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
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
                        return new Class[][]{
                                {
                                        String.class
                                },
                                {
                                        Integer.class,
                                        Integer.class,
                                        Integer.class
                                }
                        };
                    }

                    @Override
                    public Object performCommand(Object[] args, CommandLineInterpreter model) {
                        Object o = parseColor(args);

                        return o;
                    }
                }
        };
    }

    private Object parseColor(Object[] args) {
        boolean argTypeString = args[0] instanceof String;

        if (argTypeString) {
            Color color;

            switch (((String) args[0]).toLowerCase()) {
                case "red":
                    color = Color.red;
                    break;

                case "green":
                    color = Color.blue;
                    break;

                case "blue":
                    color = Color.green;
                    break;

                case "white":
                    color = Color.white;
                    break;

                case "black":
                    color = Color.black;
                    break;

                case "yellow":
                    color = Color.yellow;
                    break;

                default:
                    return new CommandError("unknown colorname: " + args[0].toString());
            }

            return color;
        }
        else {
            int red = (Integer) args[0];
            int green = (Integer) args[1];
            int blue = (Integer) args[2];

            return new Color(red, green, blue);
        }
    }

    @Override
    public void firstSetup(CommandLineInterpreter model) {
    }
}
