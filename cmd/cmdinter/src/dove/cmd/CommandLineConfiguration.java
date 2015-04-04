package dove.cmd;

import dove.config.Configuration;
import dove.config.edit.ConfigEditElem;
import dove.util.treelib.StringMap;

public class CommandLineConfiguration
        extends Configuration {
    @Override
    public StringMap<ConfigEditElem> getEditTree() {
        return null;
    }
}
