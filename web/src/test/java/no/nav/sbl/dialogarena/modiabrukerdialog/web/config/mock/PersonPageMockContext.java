package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.EnhetService;
import no.nav.personsok.consumer.fim.personsok.PersonsokServiceBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.PlukkOppgaveService;
import no.nav.sbl.dialogarena.varsel.domain.Varsel;
import no.nav.sbl.dialogarena.varsel.service.VarslerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.option.Optional.optional;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@Import({
    SykepengerWidgetMockContext.class,
    UtbetalingerMockContext.class,
    SporsmalOgSvarMockContext.class,
    SaksoversiktMockContext.class,
    ConsumerServicesMockContext.class,
    JacksonMockContext.class,
    VarslingMockContext.class
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
    public GOSYSNAVOrgEnhet gosysnavOrgEnhet() {
        return mock(GOSYSNAVOrgEnhet.class);
    }

    @Bean
    public CmsContentRetriever cmsContentRetriever() {
        CmsContentRetriever mock = mock(CmsContentRetriever.class);
        when(mock.getDefaultLocale()).thenReturn("nb");
        when(mock.hentTekst(anyString())).thenReturn("Tekst fra mock-cms");
        return mock;
    }

    @Bean
    public VarslerService varslerService() {
        VarslerService varslerServiceMock = mock(VarslerService.class);
        List<Varsel> varselList = new ArrayList<>();
        varselList.add(new Varsel("", now(), "", new ArrayList<Varsel.VarselMelding>()));
        when(varslerServiceMock.hentAlleVarsler(anyString())).thenReturn(optional(varselList));
        return varslerServiceMock;
    }
}
