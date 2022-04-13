package no.nav.modiapersonoversikt.legacy.sak.mock;

import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class KodeverkMock {
    @Bean
    public EnhetligKodeverk.Service enhetligKodeverk() {
        return mock(EnhetligKodeverk.Service.class);
    }

}
