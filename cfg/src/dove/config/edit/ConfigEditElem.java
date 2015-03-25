package dove.config.edit;

import dove.config.edit.datatypes.IntEdit;
import dove.config.edit.datatypes.StringEdit;
import dove.document.DocumentContext;

import javax.swing.*;

public abstract class ConfigEditElem {
    public String propertyName;
    public String configName;

    public ConfigEditElem(String configName, String propertyName) {
        this.propertyName = propertyName;
        this.configName = configName;
    }

    public static ConfigEditElem create(DocumentContext doc, String configName, String propertyName, Object... args) {
        Object elem = doc.config.getConfiguration(configName).get(propertyName);

        ConfigEditElem c;

        if (elem instanceof String) {
            if (args.length == 0)
                c = new StringEdit(configName, propertyName, doc);
            else
                c = new StringEdit(configName, propertyName, doc, (Integer) args[0]);

        }
        else if (elem instanceof Integer)
            switch (args.length) {
                case 2:
                    c = new IntEdit(configName, propertyName, doc, (Integer) args[0],
                            (Integer) args[1]);
                    break;

                case 3:
                    c = new IntEdit(configName, propertyName, doc, (Integer) args[0],
                            (Integer) args[1], (Integer) args[2]);
                    break;

                default:
                    if (args.length < 2)
                        throw new IllegalArgumentException("To few arguments to create an intedit");

                    c = new IntEdit(configName, propertyName, doc, (Integer) args[0],
                            (Integer) args[1], (Integer) args[2], (Integer) args[3]);
                    break;
            }
        else
            throw new IllegalArgumentException("no editelement available for this configurationtype");

        return c;
    }

    public abstract JComponent component();
}