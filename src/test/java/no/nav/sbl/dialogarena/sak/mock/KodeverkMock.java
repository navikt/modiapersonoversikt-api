package no.nav.sbl.dialogarena.sak.mock;

import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.sak.service.BulletProofKodeverkService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class KodeverkMock {

    @Bean
    public BulletProofKodeverkService bulletProofKodeverkService() {
        return mock(BulletProofKodeverkService.class);
    }

    @Bean
    public Kodeverk kodeverk() {
        return mock(Kodeverk.class);
    }

}
