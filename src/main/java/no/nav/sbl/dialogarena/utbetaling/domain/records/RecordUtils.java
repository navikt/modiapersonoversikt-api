package no.nav.sbl.dialogarena.utbetaling.domain.records;

import no.nav.sbl.dialogarena.common.records.Key;
import no.nav.sbl.dialogarena.common.records.Record;
import org.apache.commons.collections15.Transformer;

public class RecordUtils {

    public static <T> Record<T> selectKeys(Record<T> initialRecord, Key<?>... keys) {
        Record<T> newRecord = new Record<>();
        for (Key<?> key : keys) {
            if (initialRecord.containsKey(key)) {
                Object value = initialRecord.get(key);
                newRecord = newRecord.with((Key<Object>) key, value);
            }
        }
        return newRecord;
    }

    public static <T> Transformer<Record<T>, Record<T>> selectKeys(final Key<?>... keys) {
        return new Transformer<Record<T>, Record<T>>() {
            @Override
            public Record<T> transform(Record<T> initialRecord) {
                return selectKeys(initialRecord, keys);
            }
        };
    }

}
