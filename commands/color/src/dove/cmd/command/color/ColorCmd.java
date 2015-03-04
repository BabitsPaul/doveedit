package dove.cmd.command.color;

import dove.cmd.model.Command;
import dove.cmd.model.CommandArg;
import dove.cmd.model.CommandLineModel;
import dove.cmd.model.CommandLineVar;

import java.awt.*;

public class ColorCmd
        implements Command {
    private static final String MODEL_FOREGROUND = "commandline.color.foreground";

    private static final String MODEL_BACKGROUND = "commandline.color.background";

    private static final String COMMAND_NAME = "color";

    private static final String SHORT_DESCRIPTION = "Changes the foreground and backgroundcolor of the console";

    private static final String LONG_DESCRIPTION =
            SHORT_DESCRIPTION + "\n" +
                    "f - changes the foregroundcolor\n" +
                    "b - changes the backgroundcolor\n" +
                    "Colors can be created in one of the following ways:\n" +
                    "either one of the following predefined: \n" +
                    "yellow, red, green, blue, black, white \n" +
                    "or defined by an rgb-value - either hex or dec\n" +
                    "examples: \n" +
                    "color /f white /b blue - set background to blue and foreground to white\n" +
                    "color /f 0xFF 0x00 0xFF - sets the foreground to purple\n" +
                    "color /f 255 0 255 - sets the foreground to purple\n";

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
                        return "f";
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
                    public Object performCommand(Object[] args, CommandLineModel model) {
                        Object o = parseColor(args);

                        if (o instanceof String)
                            return o;

                        model.put(MODEL_FOREGROUND, o);

                        return "";
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

                default:
                    return "unknown colorname: " + args[0].toString();
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
    public void firstSetup(CommandLineModel model) {
        CommandLineVar f = new CommandLineVar(Color.white);
        f.makeTypesafe(true, Color.class);

        CommandLineVar b = new CommandLineVar(Color.blue);
        b.makeTypesafe(true, Color.class);

        model.addCommandLineVar(MODEL_FOREGROUND, f);
        model.addCommandLineVar(MODEL_BACKGROUND, b);
    }
}
