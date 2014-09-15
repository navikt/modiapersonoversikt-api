package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.OppgaveBehandlingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class ConsumerServicesMockContext {

    @Bean
    public HenvendelseUtsendingService henvendelseUtsendingService() {
        return mock(HenvendelseUtsendingService.class);
    }

    @Bean
    public OppgaveBehandlingService oppgaveBehandlingService() {
        return mock(OppgaveBehandlingService.class);
    }

    @Bean
    public AnsattService ansattService() {
        return mock(AnsattService.class);
    }

    @Bean
    public SaksbehandlerInnstillingerService saksbehandlerInnstillingerService() {
        return mock(SaksbehandlerInnstillingerService.class);
    }
}
