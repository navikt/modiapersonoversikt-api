package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingerSok;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class SporsmalOgSvarMockContext {

    @Bean
    public GsakService gsakService() {
        return mock(GsakService.class);
    }

    @Bean
    public HenvendelseBehandlingService henvendelseBehandlingService() {
        return mock(HenvendelseBehandlingService.class);
    }

    @Bean
    public MeldingerSok meldingIndekserer() {
        return mock(MeldingerSok.class);
    }
}
