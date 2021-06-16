package no.nav.modiapersonoversikt.legacy.sak.mock;

import no.nav.modiapersonoversikt.consumer.kodeverk2.Kodeverk;
import no.nav.modiapersonoversikt.legacy.sak.service.BulletproofKodeverkService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class KodeverkMock {

    @Bean
    public BulletproofKodeverkService bulletProofKodeverkService() {
        return mock(BulletproofKodeverkService.class);
    }

    @Bean
    public Kodeverk kodeverk() {
        return mock(Kodeverk.class);
    }

}
