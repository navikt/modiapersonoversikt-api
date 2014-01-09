package no.nav.sbl.dialogarena.utbetaling.domain.records;

import java.util.List;
import no.nav.sbl.dialogarena.common.records.Key;
import no.nav.sbl.dialogarena.common.records.Record;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.utbetaling.domain.records.RecordUtils.selectKeys;
import static org.junit.Assert.assertEquals;

public class RecordUtilsTest {

    private static class Keys {
        static Key<Integer> id = new Key<>("id");
        static Key<String> string = new Key<>("string");
    }

    private List<Record<Keys>> records;

    @Before
    public void init() {
        Record<Keys> record1 = new Record<Keys>().with(Keys.id, 1).with(Keys.string, "string1");
        Record<Keys> record2 = new Record<Keys>().with(Keys.id, 2).with(Keys.string, "string2");
        Record<Keys> record3 = new Record<Keys>().with(Keys.id, 3);
        Record<Keys> record4 = new Record<>();
        records = asList(record1, record2, record3, record4);
    }

    @Test
    public void selectExistingKeyTest() {
        Record<Keys> initialRecord = records.get(0);
        Record<Keys> newRecord = selectKeys(initialRecord, Keys.string);
        assertEquals(new Record<Keys>().with(Keys.string, initialRecord.get(Keys.string)), newRecord);
    }

    @Test
    public void selectNonExistingKeyTest() {
        Record<Keys> newRecord = selectKeys(records.get(0), new Key<>("non-existing"));
        assertEquals(new Record<Keys>(), newRecord);
    }

    @Test
    public void selectExistingAndNonExistingKeyTest() {
        Record<Keys> initialRecord = records.get(0);
        Record<Keys> newRecord = selectKeys(initialRecord, Keys.string, new Key<>("non-existing"));
        assertEquals(new Record<Keys>().with(Keys.string, initialRecord.get(Keys.string)), newRecord);
    }

    @Test
    public void selectKeysTransformerTest() {
        List<Record<Keys>> ids = on(records).map(RecordUtils.<Keys>selectKeys(Keys.id)).collect();
        for (int i = 0; i < records.size(); i++) {
            assertEquals(records.get(i).containsKey(Keys.id), ids.get(i).containsKey(Keys.id));
            assertEquals(records.get(i).get(Keys.id), ids.get(i).get(Keys.id));
        }
    }

}
