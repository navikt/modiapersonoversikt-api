package no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Record<A> extends HashMap<Key<?>, Object> {
	
	@SuppressWarnings("unchecked")
	public <T> T get(Key<T> key) {
		return (T) super.get(key);
	}

	public <T> Record<A> with(Key<T> key, T value) {
		Record<A> ny = new Record<A>();
		for (Key<?> k : keySet()) {
			ny.putInternal(k, get(k));
		}
		ny.putInternal(key, value);
		return ny;
	}
	
	private void putInternal(Key<?> key, Object value) {
		super.put(key, value);
	}
	
	@Override
	public Object put(Key<?> key, Object value) {
		throw new UnsupportedOperationException("Bruk Record.with");
	}

	public List<Object> valuesFor(Key<?>... keys) {
		List<Object> values = new ArrayList<>();
		for (Key<?> key : keys) {
			values.add(get(key));
		}
		return values;
	}

}


