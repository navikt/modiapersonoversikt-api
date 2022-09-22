package no.nav.modiapersonoversikt.legacy.sak.config;

import no.nav.modiapersonoversikt.legacy.sak.service.TilgangskontrollServiceImpl;
import no.nav.modiapersonoversikt.legacy.sak.service.interfaces.TilgangskontrollService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SaksoversiktServiceConfig {
    @Bean
    public TilgangskontrollService tilgangskontrollService() {
        return new TilgangskontrollServiceImpl();
    }
}
