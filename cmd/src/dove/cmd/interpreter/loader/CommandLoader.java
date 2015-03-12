package dove.cmd.interpreter.loader;

import dove.GlobalFlags;
import dove.cmd.interpreter.Command;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;

public class CommandLoader {
    private HashMap<String, String> commandJarMap = new HashMap<>();

    private HashMap<String, Command> commandMap = new HashMap<>();

    public HashMap<String, Command> getCommands() {
        return commandMap;
    }

    public CommandLoaderLog loadCommands() {
        //create logger
        CommandLoaderLog log = new CommandLoaderLog();

        //get file with the list of installed commands
        File cmdList = new File(GlobalFlags.getProperty("home") + "/system/cmd/cmd.list");

        //load the list into this instance
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cmdList));
            commandJarMap = (HashMap) ois.readObject();
            ois.close();
        }
        catch (IOException | NullPointerException | ClassNotFoundException e) {
            log.setFailedCause(e);
            log.setGeneralSuccess(false);

            return log;
        }

        //create a logelement for every command
        HashMap<String, CommandLoaderLogElement> logElements = new HashMap<>();
        for (String cmd : commandJarMap.keySet()) {
            CommandLoaderLogElement logElement = new CommandLoaderLogElement();
            logElement.commandName = cmd;
            logElement.fileName = commandJarMap.get(cmd);
            log.add(logElement);
            logElements.put(cmd, logElement);
        }

        //parse urls
        URL[] commandJars;
        ArrayList<URL> jarURLs = new ArrayList<>();
        int fileCount = 0;
        for (String file : commandJarMap.values()) {
            try {
                jarURLs.add(new File(file).toURI().toURL());
            }
            catch (MalformedURLException e) {
                CommandLoaderLogElement logElement = log.getLog().get(fileCount);
                logElement.successfull = false;
                logElement.failedCause = e;
            }

            fileCount++;
        }
        commandJars = new URL[jarURLs.size()];
        jarURLs.toArray(commandJars);

        //create a new classloader to load the listed commands
        ClassLoader jarLoader = URLClassLoader.newInstance(commandJars);

        //load classes and create commandobjects
        for (String name : commandJarMap.keySet()) {
            try {
                Class cmdClass = jarLoader.loadClass(name);
                commandMap.put(name, (Command) cmdClass.getConstructor().newInstance());
            }
            catch (InstantiationException | IllegalAccessException
                    | InvocationTargetException | NoSuchMethodException
                    | ClassNotFoundException e) {
                CommandLoaderLogElement logElement = logElements.get(name);
                logElement.failedCause = e;
                logElement.successfull = false;
            }
        }

        return log;
    }

    public CommandLoaderLog loadCommand(String className, String file) {
        CommandLoaderLog log = new CommandLoaderLog();
        log.setGeneralSuccess(true);

        CommandLoaderLogElement logElement = new CommandLoaderLogElement();
        logElement.commandName = className;
        logElement.fileName = file;
        log.add(logElement);

        try {
            URL url = new File(file).toURI().toURL();

            URLClassLoader loader = URLClassLoader.newInstance(new URL[]{url});
            Class cmdClass = loader.loadClass(className);

            commandMap.put(className, (Command) cmdClass.getConstructor().newInstance());
            commandJarMap.put(className, file);
        }
        catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException
                | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            logElement.successfull = false;
            logElement.failedCause = e;
        }

        return log;
    }
}