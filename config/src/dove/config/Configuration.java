package dove.config;

import dove.config.edit.ConfigEditElem;
import dove.util.collections.MultiMap;
import dove.util.concurrent.access.AccessTask;
import dove.util.misc.StringHelper;
import dove.util.treelib.StringMap;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Configuration
        extends StringMap<Object> {
    static HashMap<String, Configuration> availableTypes;

    static {
        if (availableTypes == null)
            availableTypes = new HashMap<>();

        //register this subtype
        //TODO
        availableTypes.put(getID(), null);
    }

    {
        //if this configuration hasn't yet been
        //registered, it will be registered now
        //id must be unique
        if (!availableTypes.containsKey(getID())) {
            availableTypes.put(getID(), this);

            ConfigLoader.loadDefaultConfiguration(this);
        }
    }

    private MultiMap<String, ConfigListener> listeners    = new MultiMap<>();
    private ArrayList<ConfigListener>        notifyAlways = new ArrayList<>();
    private StringMap<Boolean>               userAccess   = new StringMap<>(Boolean.class);

    public Configuration() {
        super(Object.class);
    }

    public static String getID() {
        //this method must always be overriden
        //since it is used to register this class
        //as a configurationclass
        throw new NotImplementedException();
    }

    protected void fireConfigChanged(String key) {
        listeners.get(key).forEach(l -> l.valueChanged(new ConfigChangedEvent(this, key)));

        notifyAlways.forEach(l -> l.valueChanged(new ConfigChangedEvent(this, key)));
    }

    public void addConfigChangedListener(ConfigListener cl, String key) {
        if (key == null)
            notifyAlways.add(cl);
        else if (!notifyAlways.contains(cl))
            listeners.add(key, cl);
    }

    public void put(String key, Object val) {
        runOpExceptionSuppressed(() -> {
            _put(key, val);
            return null;
        }, AccessTask.TaskOpType.WRITE);
    }

    protected void _put(String key, Object val) {
        _put(key, val);

        fireConfigChanged(key);
    }

    public boolean remove(String key) {
        return runOpExceptionSuppressed(() -> _remove(key), AccessTask.TaskOpType.WRITE);
    }

    protected boolean _remove(String key) {
        boolean temp = _remove(key);

        fireConfigChanged(key);

        return temp;
    }

    public Object get(String key) {
        return runOpExceptionSuppressed(() -> _get(key), AccessTask.TaskOpType.READ);
    }

    protected Object _get(String key) {
        return ((StringMap) getNodeForPath(StringHelper.castToChar(key.toCharArray()))).getVal();
    }

    public boolean userEditable(String key) {
        return userAccess.get(key);
    }

    public abstract StringMap<ConfigEditElem> getEditTree();
}