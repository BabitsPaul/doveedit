package dove;

import java.util.HashMap;

public class GlobalFlags {
    public static boolean jar_launched;

    public static String[] args;

    private static HashMap<String, String> argParse = new HashMap<>();

    public static void processLauncherArgs(String[] args) {
        jar_launched = (args.length > 0);
        jar_launched &= args[0].equals("dove_launcher");

        GlobalFlags.args = args;

        if (args.length == 0) {
            createJarLaunchedFlags();
        }

        String currentFlag = null;
        String currentValue = "";

        for (String arg : args) {
            arg = arg.trim();

            if (arg.startsWith("/")) {
                if (currentFlag != null) {
                    argParse.put(currentFlag, currentValue);
                }

                currentFlag = arg.substring(1);
                currentValue = "";
            }
            else {
                currentValue += arg;
            }
        }

        argParse.put(currentFlag, currentValue);
    }

    public static Object getProperty(String key) {
        return argParse.get(key);
    }

    private static void createJarLaunchedFlags() {
        argParse.put("open", null);
        argParse.put("launcher", "jar");
        argParse.put("os", System.getProperty("os.name"));
        argParse.put("home", System.getProperty("user.home") + "/dove/");
    }
}
