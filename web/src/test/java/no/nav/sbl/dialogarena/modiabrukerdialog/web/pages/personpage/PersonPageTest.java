package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage;

import junit.framework.Assert;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.support.EgenAnsattServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.*;
import no.nav.kjerneinfo.domain.person.fakta.AnsvarligEnhet;
import no.nav.kjerneinfo.domain.person.fakta.Organisasjonsenhet;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.tab.VisitkortTabListePanel;
import no.nav.modig.modia.lamell.ReactSjekkForlatModal;
import no.nav.modig.modia.lamell.TokenLamellPanel;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.test.EventGenerator;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.GsakKodeTema;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.PersonPageMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.LamellContainer;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.jscallback.SokOppBrukerCallback;
import org.apache.wicket.ajax.AjaxRequestHandler;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.ArrayList;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.reflect.Reflect.on;
import static no.nav.modig.modia.constants.ModiaConstants.HENT_PERSON_BEGRUNNET;
import static no.nav.modig.modia.events.InternalEvents.FODSELSNUMMER_FUNNET_MED_BEGRUNNElSE;
import static no.nav.modig.modia.events.InternalEvents.GOTO_HENT_PERSONPAGE;
import static no.nav.modig.wicket.test.FluentWicketTester.with;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.ARBD;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage.VALGT_OPPGAVE_FNR_ATTR;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage.VALGT_OPPGAVE_ID_ATTR;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersonPageMockContext.class})
public class PersonPageTest extends WicketPageTest {

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    private GsakKodeverk gsakKodeverk;

    @Inject
    private PersonKjerneinfoServiceBi personKjerneinfoServiceBi;

    @Inject
    private EgenAnsattServiceBi egenAnsattServiceBi;

    private final static String testFnr = "12037649749";

    @Before
    public void setUp() {
        when(personKjerneinfoServiceBi.hentKjerneinformasjon(any())).thenReturn(lagHentKjerneinformasjonResponse());
        when(henvendelseUtsendingService.hentTraad(anyString(), anyString())).thenReturn(asList(lagMelding()));
        when(gsakKodeverk.hentTemaListe()).thenReturn(new ArrayList<>(asList(
                new GsakKodeTema.Tema("kode", "tekst",
                        new ArrayList<>(asList(new GsakKodeTema.OppgaveType("kode", "tekst", 1))),
                        new ArrayList<>(asList(new GsakKodeTema.Prioritet("kode", "tekst"))),
                        new ArrayList<>(asList(new GsakKodeTema.Underkategori("kode", "tekst")))))));
    }

    @Test
    public void lasterPersonPageUtenFeil() {
        wicket.goTo(PersonPage.class, with().param("fnr", testFnr))
                .should().containComponent(withId("kjerneinfotabs").and(ofType(VisitkortTabListePanel.class)))
                .should().containComponent(withId("personsokPanel").and(ofType(PersonsokPanel.class)))
                .should().containComponent(withId("lameller").and(ofType(TokenLamellPanel.class)));
    }

    @Test
    public void viserModaldialVedUlagredeEndringerOgRefresh() {
        wicket.goTo(PersonPage.class, with().param("fnr", testFnr));
        PersonPage personPage = (PersonPage) wicket.tester.getLastRenderedPage();
        ReactSjekkForlatModal redirectPopup = mock(ReactSjekkForlatModal.class);
        LamellContainer lamellContainer = mock(LamellContainer.class);
        on(personPage).setFieldValue("redirectPopup", redirectPopup);
        on(personPage).setFieldValue("lamellContainer", lamellContainer);
        when(lamellContainer.hasUnsavedChanges()).thenReturn(true);

        AjaxRequestTarget target = new AjaxRequestHandler(personPage);
        personPage.refreshKjerneinfo(target, new PageParameters());

        verify(redirectPopup, times(1)).show();
        verify(redirectPopup, times(0)).redirect();
    }

    @Test
    public void viserIkkeModaldialogVedIngenUlagredeEndringerOgRefresh() {
        wicket.goTo(PersonPage.class, with().param("fnr", testFnr));
        PersonPage personPage = (PersonPage) wicket.tester.getLastRenderedPage();
        ReactSjekkForlatModal redirectPopup = mock(ReactSjekkForlatModal.class);
        LamellContainer lamellContainer = mock(LamellContainer.class);
        on(personPage).setFieldValue("redirectPopup", redirectPopup);
        on(personPage).setFieldValue("lamellContainer", lamellContainer);
        when(lamellContainer.hasUnsavedChanges()).thenReturn(false);

        AjaxRequestTarget target = new AjaxRequestHandler(personPage);
        personPage.refreshKjerneinfo(target, new PageParameters());

        verify(redirectPopup, times(0)).show();
        verify(redirectPopup, times(1)).redirect();
    }

    @Test
    public void sletterPlukketOppgaveFraSessionVedRiktigeEvents() {
        assertSletterPlukketOppgaveFraSessionVedEvent(Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER);
        assertSletterPlukketOppgaveFraSessionVedEvent(Events.SporsmalOgSvar.LEGG_TILBAKE_UTFORT);
    }

    private void assertSletterPlukketOppgaveFraSessionVedEvent(String event) {
        wicket.goTo(PersonPage.class, with().param("fnr", testFnr));
        wicket.tester.getSession().setAttribute(VALGT_OPPGAVE_FNR_ATTR, "fnr");
        wicket.tester.getSession().setAttribute(VALGT_OPPGAVE_ID_ATTR, "oppgaveid");

        wicket.sendEvent(createEvent(event));

        assertNull(wicket.tester.getSession().getAttribute(VALGT_OPPGAVE_FNR_ATTR));
        assertNull(wicket.tester.getSession().getAttribute(VALGT_OPPGAVE_ID_ATTR));
    }

    @Test
    public void oppdatererKjerneInfoVedFodselsnummerFunnetMedBegrunnelse() {
        final String newFnr = "12345612345";
        wicket.goTo(PersonPage.class, with().param("fnr", testFnr));
        wicket.tester.getSession().setAttribute(HENT_PERSON_BEGRUNNET, "");

        wicket.sendEvent(createEvent(FODSELSNUMMER_FUNNET_MED_BEGRUNNElSE, createPageParamerersWithFnr(newFnr)));

        assertEquals(newFnr, wicket.tester.getSession().getAttribute(HENT_PERSON_BEGRUNNET));
        assertFalse(wicket.tester.ifContains(newFnr).wasFailed());
        assertTrue(wicket.tester.ifContains(testFnr).wasFailed());
    }

    private PageParameters createPageParamerersWithFnr(String newFnr) {
        PageParameters pageparams = new PageParameters();
        pageparams.add("fnr", newFnr);
        return pageparams;
    }

    @Test
    public void vellykketGotoHentPersonPageBeggeError() {
        wicket.goTo(PersonPage.class, with().param("fnr", testFnr));

        wicket.sendEvent(createEvent(GOTO_HENT_PERSONPAGE, "{\"errortext\":\"Feil tekst\",\"sikkerhettiltaksbeskrivelse\":\"Farlig.\"}"));
    }

    @Test
    public void vellykketGotoHentPersonPageErrortextAndWrongFnr() {
        wicket.goTo(PersonPage.class, with().param("soektfnr", "wrongFnr").param("error", "errorMessage"))
                .should().containPatterns("wrongFnr")
                .should().containPatterns("errorMessage");

        wicket.sendEvent(createEvent(GOTO_HENT_PERSONPAGE, "{\"errortext\":\"Feil tekst\",\"soektfnr\":\"wrongFnr\"}"));
    }

    @Test
    public void vellykketGotoHentPersonPageKunErrortekst() {

        wicket.goTo(PersonPage.class, with().param("fnr", testFnr));

        wicket.sendEvent(createEvent(GOTO_HENT_PERSONPAGE, "{\"errortext\":\"Feil tekst\"}"));
    }

    @Test
    public void shouldExtractSikkerhetstiltaksbeskrivelse() throws JSONException {
        PersonPage page = new PersonPage(new PageParameters());
        String sikkerhetstiltak =
                page.getTextFromPayload("{\"errortext\":\"Feil tekst\",\"sikkerhettiltaksbeskrivelse\":\"Farlig.\"}",
                        SokOppBrukerCallback.JSON_SIKKERHETTILTAKS_BESKRIVELSE);
        assertEquals("Farlig.", sikkerhetstiltak);
    }


    @Test
    public void shouldExtractErrortext() throws JSONException {
        PersonPage page = new PersonPage(new PageParameters());
        String errorTxt =
                page.getTextFromPayload("{\"errortext\":\"Feil tekst\",\"sikkerhettiltaksbeskrivelse\":\"Farlig.\"}",
                        SokOppBrukerCallback.JSON_ERROR_TEXT);
        assertEquals("Feil tekst", errorTxt);
    }

    @Test
    public void shouldExtractErrortextAndFnr() throws JSONException {
        PersonPage page = new PersonPage(new PageParameters());
        String errorTxt =
                page.getTextFromPayload("{\"errortext\":\"Feil tekst\",\"soektfnr\":\"wrongFnr\"}",
                        SokOppBrukerCallback.JSON_ERROR_TEXT);
        String soektfnr =
                page.getTextFromPayload("{\"errortext\":\"Feil tekst\",\"soektfnr\":\"wrongFnr\"}",
                        SokOppBrukerCallback.JSON_SOKT_FNR);
        assertEquals("Feil tekst", errorTxt);
        assertEquals("wrongFnr", soektfnr);
    }

    @Test
    public void shouldExtractNullWhenFnrtExist() throws JSONException {
        PersonPage page = new PersonPage(new PageParameters());
        String sikkerhetstiltak =
                page.getTextFromPayload("{\"errortext\":\"Feil tekst\"}", SokOppBrukerCallback.JSON_SIKKERHETTILTAKS_BESKRIVELSE);
        Assert.assertNull(sikkerhetstiltak);
    }

    private EventGenerator createEvent(final String eventNavn) {
        return new EventGenerator() {
            @Override
            public Object createEvent(AjaxRequestTarget target) {
                return new NamedEventPayload(eventNavn, "");
            }
        };
    }

    private EventGenerator createEvent(final String eventNavn, final Object payload) {
        return new EventGenerator() {
            @Override
            public Object createEvent(AjaxRequestTarget target) {
                return new NamedEventPayload(eventNavn, payload);
            }
        };
    }

    private Melding lagMelding() {
        return new Melding().withId("id").withOpprettetDato(now()).withTemagruppe(ARBD.name()).withOppgaveId("id");
    }

    private HentKjerneinformasjonResponse lagHentKjerneinformasjonResponse() {
        final HentKjerneinformasjonResponse respons = new HentKjerneinformasjonResponse();
        final Person person = new Person();
        final Personfakta personfakta = new Personfakta();
        personfakta.setPersonnavn(new Personnavn.With().fornavn("etFornavn").etternavn("etEtternavn").done());
        personfakta.setAnsvarligEnhet(new AnsvarligEnhet.With().organisasjonsenhet(new Organisasjonsenhet.With().organisasjonselementId("0000").done()).done());
        person.setPersonfakta(personfakta);
        respons.setPerson(person);
        return respons;
    }
}
