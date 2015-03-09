package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.EnhetService;
import no.nav.personsok.consumer.fim.personsok.PersonsokServiceBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.PlukkOppgaveService;
import no.nav.sbl.dialogarena.sak.config.SaksoversiktServiceConfig;
import no.nav.sbl.dialogarena.sak.service.Filter;
import no.nav.sbl.dialogarena.sak.service.SaksoversiktService;
import no.nav.sbl.dialogarena.sporsmalogsvar.context.SporsmalOgSvarContext;
import no.nav.sbl.dialogarena.utbetaling.lamell.context.UtbetalingLamellContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@Import({
        EndpointMockContext.class,
        SykepengerWidgetMockContext.class,
        SaksbehandlerInnstillingerPanelMockContext.class,
        UtbetalingLamellContext.class,
        SporsmalOgSvarContext.class,
        SaksoversiktServiceConfig.class,
        ConsumerServicesMockContext.class
})
public class PersonPageMockContext {

    @Bean
    public PersonKjerneinfoServiceBi personKjerneinfoServiceBi() {
        return mock(PersonKjerneinfoServiceBi.class);
    }

    @Bean
    public SaksoversiktService saksoversiktService() {
        return mock(SaksoversiktService.class);
    }

    @Bean
    public Filter sakOgBehandlingFilter() {
        return mock(Filter.class);
    }

    @Bean(name = "pep")
    public EnforcementPoint pep() {
        return mock(EnforcementPoint.class);
    }

    @Bean
    public EnhetService enhetService() {
        EnhetService enhetService = mock(EnhetService.class);
        when(enhetService.hentEnhet(anyString())).thenReturn(new AnsattEnhet("", ""));
        return enhetService;
    }

    @Bean
    public PlukkOppgaveService plukkOppgaveService() {
        return mock(PlukkOppgaveService.class);
    }

    @Bean
    public PersonsokServiceBi personsokServiceBi() {
        return mock(PersonsokServiceBi.class);
    }

    @Bean
    public BrukerprofilServiceBi brukerprofilServiceBi() {
        return mock(BrukerprofilServiceBi.class);
    }
}
