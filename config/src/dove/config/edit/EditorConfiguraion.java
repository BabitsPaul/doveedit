package dove.config.edit;

import dove.config.Configuration;
import dove.util.treelib.StringMap;

public class EditorConfiguraion
        extends Configuration {
    public static String getID() {
        return "configeditor";
    }

    @Override
    public StringMap<ConfigEditElem> getEditTree() {
        return null;
    }
}
