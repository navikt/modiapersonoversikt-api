package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.service;

import no.nav.modig.wicket.services.HealthCheckService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg2.OrganisasjonEnhetService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak.PsakService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.PsakServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.*;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk.GsakKodeverkFraFil;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk.StandardKodeverkImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.ldap.LDAPServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.norg2.OrganisasjonEnhetServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.OrganisasjonEnhetV2ServiceImpl;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1;
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
    public OrganisasjonEnhetService organisasjonEnhetService() {
        return new OrganisasjonEnhetServiceImpl();
    }

    @Bean
    public OrganisasjonEnhetV2Service organisasjonEnhetServiceV2() {
        return new OrganisasjonEnhetV2ServiceImpl();
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

    @Bean
    public PsakService psakService(PensjonSakV1 pensjonSakV1) {
        return new PsakServiceImpl(pensjonSakV1);
    }

    @Bean
    public ScheduledAnsattListePrefetch scheduledAnsattListePrefetch() {

        return new ScheduledAnsattListePrefetch();
    }

}
