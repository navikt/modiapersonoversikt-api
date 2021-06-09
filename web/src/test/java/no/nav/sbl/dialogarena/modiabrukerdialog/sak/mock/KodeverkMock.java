package no.nav.sbl.dialogarena.modiabrukerdialog.sak.mock;

import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.BulletproofKodeverkService;
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
