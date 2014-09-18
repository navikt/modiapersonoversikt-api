package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.EnhetService;
import no.nav.sbl.dialogarena.sak.service.SakOgBehandlingFilter;
import no.nav.sbl.dialogarena.sak.service.SaksoversiktService;
import no.nav.sbl.dialogarena.sporsmalogsvar.context.SporsmalOgSvarContext;
import no.nav.sbl.dialogarena.utbetaling.lamell.context.UtbetalingLamellContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

@Configuration
@Import({
        EndpointMockContext.class,
        SykepengerWidgetMockContext.class,
        SaksbehandlerInnstillingerPanelMockContext.class,
        UtbetalingLamellContext.class,
        SporsmalOgSvarContext.class,
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
    public SakOgBehandlingFilter sakOgBehandlingFilter() {
        return mock(SakOgBehandlingFilter.class);
    }

    @Bean(name = "pep")
    public EnforcementPoint pep() {
        return mock(EnforcementPoint.class);
    }

    @Bean
    public EnhetService enhetService() {
        return mock(EnhetService.class);
    }
}
