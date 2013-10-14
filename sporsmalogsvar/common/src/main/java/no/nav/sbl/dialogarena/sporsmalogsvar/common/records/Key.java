package no.nav.sbl.dialogarena.sporsmalogsvar.common.records;

import no.nav.modig.lang.collections.PredicateUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

public class Key<T> implements Transformer<Record<?>, T> {

	public final String name;
	
	public static <T> Key<T> key(String name) {
		return new Key<>(name);
	}

	public Key(String name) {
		this.name = name;
	}

	@Override
	public T transform(Record<?> input) {
		return input.get(this);
	}

	public String toString() {
		return name;
	}

	public int hashCode() {
		return name.hashCode();
	}

	public boolean equals(Object obj) {
		return obj instanceof Key && name.equals(((Key<?>) obj).name);
	}

	public Predicate<? super Record<?>> is(T value) {
		return PredicateUtils.where(this, PredicateUtils.equalTo(value));
	}

}
