package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    public OrganisasjonEnhetV2Service organisasjonEnhetV2Service() {
        return mock(OrganisasjonEnhetV2Service.class);
    }


    @Bean
    public LDAPService ldapService() {
        LDAPService mock = mock(LDAPService.class);
        when(mock.hentSaksbehandler(anyString())).thenReturn(new Saksbehandler("Daniel", "Franck", "ident"));
        return mock;
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
    public KodeverkmanagerBi kodeverk() {
        return mock(KodeverkmanagerBi.class);
    }
}
