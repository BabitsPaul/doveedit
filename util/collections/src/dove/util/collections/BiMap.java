package dove.util.collections;

import java.util.HashMap;
import java.util.Map;

public class BiMap<T_A, T_B> {
    private HashMap<T_A, T_B> a_to_b;
    private HashMap<T_B, T_A> b_to_a;

    private Class<T_A> class_a;
    private Class<T_B> class_b;

    public BiMap(HashMap map, Class<T_A> class_a, Class<T_B> class_b) {
        this(class_a, class_b);

        if (map.isEmpty())
            return;

        Map.Entry entry = (Map.Entry) map.entrySet().iterator().next();
        boolean isA_to_B = class_a.isInstance(entry.getKey());

        HashMap applyReverse;
        if (isA_to_B) {
            applyReverse = b_to_a;

            a_to_b.putAll(map);
        } else {
            applyReverse = a_to_b;

            b_to_a.putAll(map);
        }

        map.entrySet().forEach(en -> {
            Map.Entry e = (Map.Entry) en;

            if (applyReverse.put(e.getValue(), e.getKey()) != null)
                throw new IllegalStateException("values in map must be unique to ensure complete map");
        });
    }

    public BiMap(Class<T_A> class_a, Class<T_B> class_b) {
        this.class_a = class_a;
        this.class_b = class_b;

        a_to_b = new HashMap<>();
        b_to_a = new HashMap<>();
    }

    public Object[] put(T_A a, T_B b) {
        T_B tmpB = a_to_b.put(a, b);
        T_A tmpA = b_to_a.put(b, a);

        //update other hashmap, if one of the values was overwritten
        if (!a.equals(b))
            if (tmpA != null && tmpB == null)
                a_to_b.remove(tmpA);
            else if (tmpA == null && tmpB != null)
                b_to_a.remove(tmpB);

        return new Object[]{tmpA, tmpB};
    }

    public Object get(Object o) {
        if (class_a.isInstance(o))
            return a_to_b.get(o);
        else if (class_b.isInstance(o))
            return b_to_a.get(o);
        else
            throw new ClassCastException("Key doesn't match any type");
    }
}
