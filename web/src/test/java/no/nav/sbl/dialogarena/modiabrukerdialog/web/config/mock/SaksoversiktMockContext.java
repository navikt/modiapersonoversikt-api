package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.sbl.dialogarena.sak.service.interfaces.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.transformers.FilterImpl;
import no.nav.sbl.dialogarena.saksoversikt.service.service.DataFletter;
import no.nav.sbl.dialogarena.saksoversikt.service.service.HenvendelseService;
import no.nav.sbl.dialogarena.saksoversikt.service.service.SakOgBehandlingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class SaksoversiktMockContext {

    @Bean
    public SaksoversiktService saksoversiktService() {
        return mock(SaksoversiktService.class);
    }

    @Bean
    public FilterImpl sakOgBehandlingFilter() {
        return mock(FilterImpl.class);
    }

    @Bean
    public SakOgBehandlingService sakOgBehandlingService() {
        return mock(SakOgBehandlingService.class);
    }

    @Bean
    public HenvendelseService henvendelseService() {
        return mock(HenvendelseService.class);
    }

    @Bean
    public DataFletter dataFletter() {
        return mock(DataFletter.class);
    }
}
