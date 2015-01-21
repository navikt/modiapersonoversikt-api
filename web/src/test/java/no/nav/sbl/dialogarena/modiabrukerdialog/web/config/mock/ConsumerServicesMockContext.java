package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService;
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

    @Bean
    public StandardKodeverk standardKodeverk() {
        return mock(StandardKodeverk.class);
    }

    @Bean
    public GsakKodeverk gsakKodeverk() {
        return mock(GsakKodeverk.class);
    }

    @Bean
    public SakerService sakerService() {
        return mock(SakerService.class);
    }

    @Bean
    public LokaltKodeverk lokaltKodeverk() {
        return mock(LokaltKodeverk.class);
    }

}
