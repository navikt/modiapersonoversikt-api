package no.nav.kjerneinfo.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.DefaultBaseTypeLimitingValidator;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import static org.junit.Assert.assertEquals;

public class SnapshotRule extends TestWatcher {
    private String path;
    private String name;
    private int counter = 0;
    private boolean hadMissingFile;
    private static final ObjectMapper json = new ObjectMapper()
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .activateDefaultTyping(new DefaultBaseTypeLimitingValidator());

    public SnapshotRule(String path) {
        if (path.endsWith("/")) {
            this.path = path;
        } else {
            this.path = path + "/";
        }
        new File(this.path).mkdirs();
    }

    public SnapshotRule() {
        this("src/test/resources/snapshots");
    }

    @Override
    protected void starting(Description description) {
        this.name = description.getTestClass().getName() + "::" + description.getMethodName();
    }

    @Override
    protected void finished(Description description) {
        if (this.hadMissingFile) {
            throw new RuntimeException("Snapshot did not exist, created. Rerun to verify.");
        }
    }

    public void updateSnapshot(Object object) {
        try {
            File file = this.getFile(counter++);
            if (readSnapshot(file).equals(createSnapshot(object))) {
                throw new RuntimeException("Can not update snapshot if they are already equal.");
            } else {
                save(file, object);
                throw new RuntimeException("Snapshot updated, replace call with call to `assertMatches`");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void assertMatches(Object object) {
        assertMatches(this.getFile(counter++), object);
    }


    private void assertMatches(File file, Object object) {
        try {
            assertEquals(readSnapshot(file), createSnapshot(object));
        } catch (NoSuchFileException e) {
            save(file, object);
            assertMatches(file, object);
            this.hadMissingFile = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getFile(int id) {
        if (name == null) {
            throw new IllegalStateException("No name...");
        }
        return new File(this.path + this.name + "-" + id + ".json");
    }

    private void save(File file, Object object) {
        try {
            Files.write(file.toPath(), createSnapshot(object).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String readSnapshot(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }

    private static String createSnapshot(Object object) {
        try {
            return json.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
