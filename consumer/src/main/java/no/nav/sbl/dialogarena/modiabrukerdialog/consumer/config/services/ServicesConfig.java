package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import no.nav.modig.wicket.services.HealthCheckService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.EnhetService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SaksbehandlerInnstillingerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MODIA ønsker å selv wire inn sine komponenters kontekster for å ha full kontroll over springoppsettet.
 */
@Configuration
public class ServicesConfig {

    @Bean
    public HenvendelseUtsendingService henvendelseUtsendingService() {
        return new HenvendelseUtsendingService();
    }

    @Bean
    public OppgaveBehandlingService oppgaveBehandlingService() {
        return new OppgaveBehandlingService();
    }

    @Bean
    public AnsattService ansattService() {
        return new AnsattService();
    }

    @Bean
    public EnhetService enhetService() {
        return new DefaultEnhetService();
    }

    @Bean
    public SaksbehandlerInnstillingerService saksbehandlerInnstillingerService() {
        return new DefaultSaksbehandlerInnstillingerService();
    }

    @Bean
    public HealthCheckService healthCheckService() {
        return new HealthCheckService();
    }
}
