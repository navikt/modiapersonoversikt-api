package no.nav.sbl.dialogarena.sporsmalogsvar;

import java.util.HashMap;
import java.util.Map;

public class Utils {
	
	public static <K, V> Map<K, V> map(K key, V value) {
		Map<K, V> map = new HashMap<>();
		map.put(key, value);
		return map;
	}
	
}
