package no.nav.sbl.dialogarena;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;


public final class SystemProperties {

    private SystemProperties() {
    }

    // CHECKSTYLE:OFF
    public static Properties load(String resourcePath) throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream =  Properties.class.getResourceAsStream(resourcePath)) {
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            System.setProperty((String) entry.getKey(), (String) entry.getValue());
        }
        return properties;
    }

}