package dove.util.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MapHelper {
    public static <K, V> K getFirstFor(HashMap<K, V> map, V val) {
        Optional<Map.Entry<K, V>> opt = map.entrySet().stream().
                filter(e -> e.getValue().equals(val)).findAny();

        return (opt.isPresent() ? opt.get().getKey() : null);
    }
}
