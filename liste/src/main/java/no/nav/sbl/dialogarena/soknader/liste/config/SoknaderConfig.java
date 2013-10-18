package no.nav.sbl.dialogarena.soknader.liste.config;

import no.nav.sbl.dialogarena.soknader.service.SoknaderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SoknaderConfig {

    @Bean
    public SoknaderService soknaderService(){
        return new SoknaderService();
    }
}
