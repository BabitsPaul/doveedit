package dove.config;

import dove.Resources;
import dove.document.DocumentContext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class ConfigLoader {
    private static String path = System.getProperty("user.home") + "/dove_edit/setup.cfg";
    public HashMap<String, Configuration> cfgMap;

    public ConfigLoader() {
        cfgMap = new HashMap<>();
    }

    public static void loadDefaultConfiguration(Configuration cfg) {
        //TODO
    }

    public void load(DocumentContext doc) {
        try {
            doc.resources.open(path, Resources.OpenMode.IN_ONLY);
            ObjectInputStream ois = new ObjectInputStream(doc.resources.getInputStream(path));

            int objectCount = ois.read();

            while ((objectCount--) > 0) {
                String key = (String) ois.readObject();
                Configuration.availableTypes.put(key, (Configuration) ois.readObject());
            }

            ois.close();
        }
        catch (IOException | ClassNotFoundException e) {
            doc.error.handleException("Error while loading componentconfiguration", e);
        }
        catch (Exception e) {
            doc.error.handleException("Configurationfile damaged", e);
        }
    }

    public void save(DocumentContext doc) {
        try {
            doc.resources.open(path, Resources.OpenMode.OUT_ONLY);
            ObjectOutputStream oos = new ObjectOutputStream(doc.resources.getOutputStream(path));

            oos.write(cfgMap.size());

            for (String key : cfgMap.keySet()) {
                oos.writeObject(key);
                oos.writeObject(Configuration.availableTypes.get(key));
            }

            oos.flush();
            oos.close();
        }
        catch (IOException e) {
            doc.error.handleException("Error while saving component configuration", e);
        }
    }

    public Configuration getConfiguration(String id) {
        return Configuration.availableTypes.get(id);
    }
}
