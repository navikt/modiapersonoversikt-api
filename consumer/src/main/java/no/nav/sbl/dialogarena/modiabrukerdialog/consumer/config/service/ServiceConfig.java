package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.service;

import no.nav.modig.wicket.services.HealthCheckService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.EnhetService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.DefaultEnhetService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.DefaultSaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MODIA ønsker å selv wire inn sine komponenters kontekster for å ha full kontroll over springoppsettet.
 */
@Configuration
public class ServiceConfig {

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
