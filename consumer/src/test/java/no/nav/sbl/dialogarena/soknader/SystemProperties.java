package no.nav.sbl.dialogarena.soknader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import static java.lang.System.setProperty;

public final class SystemProperties {

    private SystemProperties() { }

    public static Properties load(String resourcePath) {
        Properties properties = new Properties();
        try (InputStream inputStream =  Properties.class.getResourceAsStream(resourcePath)) {
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            setProperty((String) entry.getKey(), (String) entry.getValue());
        }
        return properties;
    }
}

