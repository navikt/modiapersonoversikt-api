package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util;

import java.util.*;

import static java.util.Collections.*;

public class Collections {

    private Collections() {}

    public static <K, V> Map<K, V> asMap(Object... keyValuePairs) {
        return unmodifiableMap(asMap(new HashMap<K, V>(), keyValuePairs));
    }

    public static <K, V> Map<K, V> asMap(Map<K, V> map, Object... keyValuePairs) {
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            map.put((K) keyValuePairs[i], (V) keyValuePairs[i + 1]);
        }
        return map;
    }

    public static <T> List<T> asList(T... values) {
        return unmodifiableList(asList(new ArrayList<T>(), values));
    }

    public static <T> List<T> asList(List<T> list, T... values) {
        for (T value : values) {
            list.add(value);
        }
        return list;
    }

    public static <T> Set<T> asSet(T... values) {
        return unmodifiableSet(asSet(new HashSet<T>(), values));
    }

    public static <T> Set<T> asSet(Set<T> set, T... values) {
        for (T value : values) {
            set.add(value);
        }
        return set;
    }
}
