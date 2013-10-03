package no.nav.sbl.dialogarena.soknader.context;

import no.nav.sbl.dialogarena.soknader.service.SoknaderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SoknaderContext {

    @Bean
    public SoknaderService soknaderService() {
        return new SoknaderService();
    }
}
