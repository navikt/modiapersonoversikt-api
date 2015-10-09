package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class SaksbehandlerInnstillingerPanelMockContext {
    @Bean
    public SaksbehandlerInnstillingerService saksbehandlerInnstillingerService() {
        return mock(SaksbehandlerInnstillingerService.class);
    }

    @Bean
    public AnsattService ansattService() {
        return mock(AnsattService.class);
    }
}
