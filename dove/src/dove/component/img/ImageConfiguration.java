package dove.component.img;

import dove.config.Configuration;
import dove.config.edit.ConfigEditElem;
import dove.util.treelib.StringMap;

public class ImageConfiguration
        extends Configuration {
    public static String getID() {
        return "img";
    }

    @Override
    public StringMap<ConfigEditElem> getEditTree() {
        return null;
    }
}
