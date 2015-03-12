package dove.cmd.interpreter;

import dove.cmd.interpreter.loader.CommandLoader;
import dove.util.misc.StringHelper;
import dove.util.treelib.StringMap;
import dove.util.treelib.TreeMap;

public class CommandLineInterpreter {
    private StringMap<CommandLineVar> vars;

    private StringMap<Command> commands;

    private CommandLoader loader;

    public CommandLineInterpreter() {
        loader = new CommandLoader();
        loader.loadCommands();

        vars = new StringMap<>(CommandLineVar.class);
        commands = new StringMap<>(Command.class);
    }

    public void doCommand(String cmd) {
        //${val/cmd]} replace with result of reading val
        // / executing command
    }

    public Object get(String key) {
        TreeMap<Character, CommandLineVar> node =
                (TreeMap<Character, CommandLineVar>) vars.getNodeForPath(StringHelper.castToChar(key.toCharArray()));

        if (node == null)
            return null;
        else
            return node.getVal().getVal();
    }

    public void put(String key, Object val) {
        CommandLineVar var = vars.get(key);

        if (var == null) {
            var = new CommandLineVar(val);
            vars.put(key, var);
        }

        var.setVal(val);
    }

    public void addCommandLineVar(String key, CommandLineVar var) {
        vars.put(key, var);
    }
}
