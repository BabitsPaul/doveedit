package dove.util.treelib;

import dove.util.concurrent.access.AccessTask;
import dove.util.misc.StringHelper;

public class StringMap<V>
        extends TreeMap<Character, V> {
    public StringMap(Class<V> valClazz) {
        super(Character.class, valClazz);
    }

    public void put(String key, V value) {
        runOpExceptionSuppressed(() -> {
            _put(key, value);
            return null;
        }, AccessTask.TaskOpType.WRITE);
    }

    protected void _put(String key, V value) {
        _put(StringHelper.castToChar(key.toCharArray()), value);
    }

    public V get(String key) {
        return runOpExceptionSuppressed(() -> _get(key), AccessTask.TaskOpType.READ);
    }

    protected V _get(String key) {
        return _get(StringHelper.castToChar(key.toCharArray()));
    }

    public boolean remove(String key) {
        return runOpExceptionSuppressed(() -> _remove(key), AccessTask.TaskOpType.WRITE);
    }

    protected boolean _remove(String key) {
        return _remove(StringHelper.castToChar(key.toCharArray()));
    }
}