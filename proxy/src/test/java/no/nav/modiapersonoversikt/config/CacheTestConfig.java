package no.nav.modiapersonoversikt.config;

import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.OppfolgingskontraktService;
import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.OppfolgingskontraktServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class CacheTestConfig {

    @Bean
    public OppfolgingskontraktService oppfolgingskontraktService() {
        return mock(OppfolgingskontraktServiceImpl.class);
    }
}
