package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.personsok.consumer.fim.personsok.PersonsokServiceBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.plukkoppgave.PlukkOppgaveService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@Import(value = {SaksbehandlerInnstillingerPanelMockContext.class})
public class HentPersonPanelMockContext {

    @Bean
    public PersonKjerneinfoServiceBi personKjerneinfoServiceBi() {
        return mock(PersonKjerneinfoServiceBi.class);
    }

    @Bean(name = "pep")
    public EnforcementPoint pep() {
        return mock(EnforcementPoint.class);
    }

    @Bean
    public PlukkOppgaveService plukkOppgaveService() {
        return mock(PlukkOppgaveService.class);
    }

    @Bean
    public OppgaveBehandlingService oppgaveBehandlingService() {
        return mock(OppgaveBehandlingService.class);
    }

    @Bean
    public OrganisasjonEnhetV2Service organisasjonEnhetV2Service() {
        return mock(OrganisasjonEnhetV2Service.class);
    }

    @Bean
    public HenvendelseUtsendingService henvendelseUtsendingService() {
        return mock(HenvendelseUtsendingService.class);
    }

    @Bean
    public PersonsokServiceBi personsokServiceBi() {
        return mock(PersonsokServiceBi.class);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return mock(ObjectMapper.class);
    }

    @Bean
    UnleashService unleashService() { return mock(UnleashService.class); }

    @Bean
    public LDAPService ldapService() {
        LDAPService mock = mock(LDAPService.class);
        when(mock.hentSaksbehandler(anyString())).thenReturn(new Saksbehandler("Test", "McTest", "ident"));
        return mock;
    }
}
