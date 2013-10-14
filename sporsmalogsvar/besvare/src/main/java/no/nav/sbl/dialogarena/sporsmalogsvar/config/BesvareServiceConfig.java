package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareService;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BesvareServiceConfig {

    @Configuration
    public static class Default {
        @Bean
        BesvareService besvareService() {
            return new BesvareServiceImpl();
        }
    }
}