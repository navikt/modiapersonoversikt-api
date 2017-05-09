package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg2.OrganisasjonEnhetService;
import no.nav.personsok.consumer.fim.personsok.PersonsokServiceBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.PlukkOppgaveService;
import org.springframework.context.annotation.*;

import static no.nav.modig.lang.option.Optional.optional;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@Import({
        SykepengerWidgetMockContext.class,
        UtbetalingerMockContext.class,
        SporsmalOgSvarMockContext.class,
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
    public OrganisasjonEnhetService organisasjonEnhetService() {
        OrganisasjonEnhetService organisasjonEnhetService = mock(OrganisasjonEnhetService.class);
        when(organisasjonEnhetService.hentEnhetGittGeografiskNedslagsfelt(anyString())).thenReturn(optional(new AnsattEnhet("", "")));
        when(organisasjonEnhetService.hentEnhetGittEnhetId(anyString())).thenReturn(optional(new AnsattEnhet("", "")));
        return organisasjonEnhetService;
    }

    @Bean
    public PlukkOppgaveService plukkOppgaveService() {
        return mock(PlukkOppgaveService.class);
    }

    @Bean
    public PersonKjerneinfoServiceBi personKjerneinfoServiceBi() {
        PersonKjerneinfoServiceBi personKjerneinfoServiceBi = mock(PersonKjerneinfoServiceBi.class);
        when(personKjerneinfoServiceBi.hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class))).thenReturn(lagMockKjerneinfoResponse());
        return personKjerneinfoServiceBi;
    }

    private HentKjerneinformasjonResponse lagMockKjerneinfoResponse() {
        Personfakta personFakta = new Personfakta();
        Person person = new Person();
        person.setPersonfakta(personFakta);
        HentKjerneinformasjonResponse hentKjerneinformasjonResponse = new HentKjerneinformasjonResponse();
        hentKjerneinformasjonResponse.setPerson(person);
        return hentKjerneinformasjonResponse;
    }

    @Bean
    public PersonsokServiceBi personsokServiceBi() {
        return mock(PersonsokServiceBi.class);
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
}
