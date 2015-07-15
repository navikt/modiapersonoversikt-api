package no.nav.sbl.dialogarena.reactkomponenter.utils.wicket;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;

public class SerializeUtils {
    public static String serialize(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, obj);
        } catch (IOException e) {
            return "";
        }
        return sw.toString();
    }

    public static <T> T deserialize(String string, Class<T> type) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(string.getBytes(), type);
        } catch (IOException e) {
            return null;
        }
    }
}
