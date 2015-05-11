package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.EnhetService;
import no.nav.personsok.consumer.fim.personsok.PersonsokServiceBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.PlukkOppgaveService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@Import({
        SykepengerWidgetMockContext.class,
        UtbetalingerMockContext.class,
        SporsmalOgSvarMockContext.class,
        SaksoversiktMockContext.class,
        ConsumerServicesMockContext.class
})
public class PersonPageMockContext {

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
    public PersonKjerneinfoServiceBi personKjerneinfoServiceBi() {
        return mock(PersonKjerneinfoServiceBi.class);
    }

    @Bean
    public PersonsokServiceBi personsokServiceBi() {
        return mock(PersonsokServiceBi.class);
    }

    @Bean
    public BrukerprofilServiceBi brukerprofilServiceBi() {
        return mock(BrukerprofilServiceBi.class);
    }

    @Bean
    public CmsContentRetriever cmsContentRetriever() {
        CmsContentRetriever mock = mock(CmsContentRetriever.class);
        when(mock.getDefaultLocale()).thenReturn("nb");
        when(mock.hentTekst(anyString())).thenReturn("Tekst fra mock-cms");
        return mock;
    }
}
