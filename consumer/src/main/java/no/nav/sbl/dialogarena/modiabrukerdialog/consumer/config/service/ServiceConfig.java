package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.service;

import no.nav.modig.wicket.services.HealthCheckService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.*;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.*;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk.GsakKodeverkFraFil;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk.StandardKodeverkImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.ldap.LDAPServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * MODIA ønsker å selv wire inn sine komponenters kontekster for å ha full kontroll over springoppsettet.
 */
@Configuration
@EnableScheduling
public class ServiceConfig {

    @Bean
    public LDAPService ldapService() {
        return new LDAPServiceImpl();
    }

    @Bean
    public HenvendelseUtsendingService henvendelseUtsendingService() {
        return new HenvendelseUtsendingServiceImpl();
    }

    @Bean
    public OppgaveBehandlingService oppgaveBehandlingService() {
        return new OppgaveBehandlingServiceImpl();
    }

    @Bean
    public AnsattService ansattService() {
        return new AnsattServiceImpl();
    }

    @Bean
    public EnhetService enhetService() {
        return new EnhetServiceImpl();
    }

    @Bean
    public SaksbehandlerInnstillingerService saksbehandlerInnstillingerService() {
        return new SaksbehandlerInnstillingerServiceImpl();
    }

    @Bean
    public HealthCheckService healthCheckService() {
        return new HealthCheckService();
    }

    @Bean
    public StandardKodeverk standardKodeverk() {
        return new StandardKodeverkImpl();
    }

    @Bean
    public GsakKodeverk gsakKodeverk() {
        return new GsakKodeverkFraFil();
    }

    @Bean
    public SakerService sakerService() {
        return new SakerServiceImpl();
    }

}
