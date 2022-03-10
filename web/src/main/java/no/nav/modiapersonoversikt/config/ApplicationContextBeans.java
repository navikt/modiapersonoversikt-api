package no.nav.modiapersonoversikt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.modiapersonoversikt.consumer.ldap.LDAPService;
import no.nav.modiapersonoversikt.infrastructure.cache.CacheConfig;
import no.nav.modiapersonoversikt.consumer.abac.AbacClient;
import no.nav.modiapersonoversikt.legacy.api.service.HenvendelseLesService;
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService;
import no.nav.modiapersonoversikt.service.unleash.UnleashService;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@Import({
        ConsumerContext.class,
        CacheConfig.class
})
public class ApplicationContextBeans {
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JacksonConfig.mapper;
    }

    @Bean
    public Tilgangskontroll tilgangskontroll(
            AbacClient abacClient,
            LDAPService ldapService,
            AnsattService ansattService,
            HenvendelseLesService henvendelseLesService,
            UnleashService unleashService
    ) {
        TilgangskontrollContext context = new TilgangskontrollContextImpl(
                abacClient,
                ldapService,
                ansattService,
                henvendelseLesService,
                unleashService
        );
        return new Tilgangskontroll(context);
    }
}
