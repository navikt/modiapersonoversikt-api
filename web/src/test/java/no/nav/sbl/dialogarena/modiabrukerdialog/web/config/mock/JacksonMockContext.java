package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonMockContext {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
