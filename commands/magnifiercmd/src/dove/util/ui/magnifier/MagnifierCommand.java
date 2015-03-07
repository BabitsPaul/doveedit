package dove.util.ui.magnifier;

import dove.cmd.interpreter.Command;
import dove.cmd.interpreter.CommandArg;
import dove.cmd.interpreter.CommandLineModel;

public class MagnifierCommand
        implements Command {
    private static final String SHORT_DESCRIPTION =
            "Creates a Magnifier";

    private static final String       LONG_DESCRIPTION =
            SHORT_DESCRIPTION + "\n" +
                    "Arguments: \n" +
                    "open - opens the magnifier with recent size and magnification rate\n" +
                    "close - closes the magnifier\n" +
                    "resize - resizes the magnifier. example: magnifier /resize 100 200 sets the magnifiersize to" +
                    "width = 100px and height = 200px\n" +
                    "rate - changes the magnification rate. example: magnifier /rate 2.0 sets the magnificationrate" +
                    "to 2";
    private static final String       MAGNIFIER_FRAME  = "frame.magnifier";
    private static final CommandArg[] COMMAND_ARG      = new CommandArg[]
            {
                    new CommandArg() {
                        @Override
                        public String commandArg() {
                            return "resize";
                        }

                        @Override
                        public Class[][] listArgTypes() {
                            return new Class[][]{
                                    {
                                            Integer.class,
                                            Integer.class
                                    }
                            };
                        }

                        @Override
                        public Object performCommand(Object[] args, CommandLineModel model) {
                            int width = (Integer) args[0];
                            int height = (Integer) args[1];

                            Magnifier magnifier = (Magnifier) model.get(MAGNIFIER_FRAME);

                            if (width < 0 || height < 0)
                                return "Invalid bounds - width and height must be > 0";

                            magnifier.setSize(width, height);

                            return "";
                        }
                    },

                    new CommandArg() {
                        @Override
                        public String commandArg() {
                            return "rate";
                        }

                        @Override
                        public Class[][] listArgTypes() {
                            return new Class[][]{
                                    {
                                            Double.class
                                    }
                            };
                        }

                        @Override
                        public Object performCommand(Object[] args, CommandLineModel model) {
                            Double rate = (Double) args[0];

                            Magnifier magnifier = (Magnifier) model.get(MAGNIFIER_FRAME);

                            if (rate == 0)
                                return "Invalid argument - magnificationrate must not be 0";

                            magnifier.setMagnificationFactor(rate);

                            return "";
                        }
                    },

                    new CommandArg() {
                        @Override
                        public String commandArg() {
                            return "close";
                        }

                        @Override
                        public Class[][] listArgTypes() {
                            return new Class[0][0];
                        }

                        @Override
                        public Object performCommand(Object[] args, CommandLineModel model) {
                            Magnifier magnifier = (Magnifier) model.get(MAGNIFIER_FRAME);

                            if (!magnifier.isVisible())
                                return "The magnifier is already closed";

                            magnifier.setVisible(false);

                            return "";
                        }
                    },

                    new CommandArg() {
                        @Override
                        public String commandArg() {
                            return "open";
                        }

                        @Override
                        public Class[][] listArgTypes() {
                            return new Class[0][0];
                        }

                        @Override
                        public Object performCommand(Object[] args, CommandLineModel model) {
                            Magnifier magnifier = (Magnifier) model.get(MAGNIFIER_FRAME);

                            if (magnifier.isVisible())
                                return "The magnifier is already opened";

                            magnifier.setVisible(true);

                            return "";
                        }
                    }
            };

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
        return COMMAND_ARG;
    }

    @Override
    public String getCommandName() {
        return "magnifier";
    }

    @Override
    public void firstSetup(CommandLineModel model) {
        model.put("magnifier.width", 100);
        model.put("magnifier.height", 100);
        model.put("magnifier.rate", 2.0);
    }
}
