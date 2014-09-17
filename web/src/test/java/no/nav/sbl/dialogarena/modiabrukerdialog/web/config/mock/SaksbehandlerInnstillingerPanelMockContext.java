package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.DefaultSaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GosysNavAnsattPortTypeMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

@Configuration
@Import(GosysNavAnsattPortTypeMock.class)
public class SaksbehandlerInnstillingerPanelMockContext {
    @Bean
    public DefaultSaksbehandlerInnstillingerService saksbehandlerInnstillingerService() {
        return mock(DefaultSaksbehandlerInnstillingerService.class);
    }

    @Bean
    public AnsattService ansattService() {
        return mock(AnsattService.class);
    }
}
