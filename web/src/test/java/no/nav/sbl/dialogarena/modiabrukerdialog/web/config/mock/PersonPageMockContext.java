package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.kjerneinfo.consumer.egenansatt.EgenAnsattService;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.modig.content.ContentRetriever;
import no.nav.personsok.consumer.fim.personsok.PersonsokServiceBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppfolgingsinfo.Oppfolgingsinfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsinfoApiService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.GrunninfoService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.GrunninfoServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.utils.WicketInjectablePropertyResolver;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.plukkoppgave.PlukkOppgaveService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    public OrganisasjonEnhetV2Service organisasjonEnhetV2Service() {
        OrganisasjonEnhetV2Service organisasjonEnhetService = mock(OrganisasjonEnhetV2Service.class);
        when(organisasjonEnhetService.hentEnhetGittEnhetId(anyString(), any())).thenReturn(Optional.of(new AnsattEnhet("", "")));
        return organisasjonEnhetService;
    }

    @Bean
    public OppfolgingsinfoApiService oppfolgingsinfoService() {
        OppfolgingsinfoApiService oppfolgingsinfoService = mock(OppfolgingsinfoApiService.class);
        when(oppfolgingsinfoService.hentOppfolgingsinfo(anyString(), any()))
                .thenReturn(new Oppfolgingsinfo(true)
                        .withVeileder(new Saksbehandler("Daniel", "Testfamilien", "Z900001"))
                        .withOppfolgingsenhet(new AnsattEnhet("0118", "NAV Aremark")));
        return oppfolgingsinfoService;
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

    @Bean
    public EgenAnsattService egenAnsattServiceBi(){
        EgenAnsattService egenAnsattService = mock(EgenAnsattService.class);
        when(egenAnsattService.erEgenAnsatt(any(String.class))).thenReturn(true);
        return egenAnsattService;
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
    public ContentRetriever cmsContentRetriever() {
        ContentRetriever mock = mock(ContentRetriever.class);
        when(mock.getDefaultLocale()).thenReturn("nb");
        when(mock.hentTekst(anyString())).thenReturn("Tekst fra mock-cms");
        return mock;
    }

    @Bean
    public GrunninfoService grunninfoService() {
        return new GrunninfoServiceImpl();
    }

    @Bean
    public WicketInjectablePropertyResolver wicketInjectablePropertyResolver() {
        WicketInjectablePropertyResolver mock = mock(WicketInjectablePropertyResolver.class);
        when(mock.getProperty(anyString())).thenReturn("Property");
        return mock;
    }


    @Bean
    public UnleashService unleashService() {
        return mock(UnleashService.class);
    }


    @Bean
    public ContentRetriever propertyResolver() {
        return mock(ContentRetriever.class);
    }

    private HentKjerneinformasjonResponse lagMockKjerneinfoResponse() {
        Personfakta personFakta = new Personfakta();
        Person person = new Person();
        person.setPersonfakta(personFakta);
        HentKjerneinformasjonResponse hentKjerneinformasjonResponse = new HentKjerneinformasjonResponse();
        hentKjerneinformasjonResponse.setPerson(person);
        return hentKjerneinformasjonResponse;
    }
}
