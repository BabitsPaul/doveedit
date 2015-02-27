package dove;

import dove.error.ErrorHandler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Installer {
    private static HashMap<String, File> fileMap = new HashMap<>();

    static {
        fileMap.put("dove/config", new File(System.getProperty("user.home") + "/dove_edit/setup.cfg"));
    }

    public static void install() {
        //setup files
        for (String key : fileMap.keySet()) {
            try {
                boolean success = fileMap.get(key).getParentFile().mkdirs();
                success &= fileMap.get(key).createNewFile();

                if (!success)
                    throw new IOException("file key: " + key + " - unknown error");
            }
            catch (IOException e) {
                new ErrorHandler(null).handleException("failed to create file", e);
            }
        }
    }

    public static void uninstall() {
        //remove files
        for (String key : fileMap.keySet()) {
            boolean success = fileMap.get("dove/config").delete();
            if (!success)
                new ErrorHandler(null).handleException("failed to delete file",
                        new IOException("file key: " + key + " - unknown error"));
        }
    }
}
