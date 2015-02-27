package dove.frame;

import dove.config.Configuration;
import dove.config.edit.ConfigEditElem;
import dove.util.treelib.StringMap;

public class FrameConfig
        extends Configuration {
    //TODO generate stringid in other way
    public static String getID() {
        return "dove/frame";
    }

    @Override
    public StringMap<ConfigEditElem> getEditTree() {
        return null;
    }
}
