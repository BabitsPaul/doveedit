package dove.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MultiMap<Key, Val>
        extends HashMap<Key, ArrayList<Val>> {
    public void add(Key key, Val val) {
        super.get(key).add(val);
    }

    public void addAll(Key key, Collection<? extends Val> val) {
        super.get(key).addAll(val);
    }

    public boolean remove(Object key, Object val) {
        return get(key).remove(val);
    }

    public void removeAll(Key key, Collection<? extends Val> val) {
        super.get(key).removeAll(val);
    }

    public Val getFirst(Key key) {
        return get(key).get(0);
    }

    public ArrayList<Val> getAll(Key key) {
        return get(key);
    }
}