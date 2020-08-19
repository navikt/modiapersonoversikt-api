package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.module.kotlin.KotlinModule;

import java.io.IOException;
import java.time.LocalDate;

public class JacksonConfig {
    public static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JodaModule());
        mapper.registerModule(new KotlinModule().addDeserializer(LocalDate.class, new JsonDeserializer<LocalDate>() {
            @Override
            public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
                return LocalDate.parse(jsonParser.getText());
            }
        }));
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
