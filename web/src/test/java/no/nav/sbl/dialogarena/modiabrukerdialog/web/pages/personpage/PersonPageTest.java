package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage;

import junit.framework.Assert;
import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.tab.VisitkortTabListePanel;
import no.nav.modig.modia.lamell.TokenLamellPanel;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.test.EventGenerator;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GsakKodeTema;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.GsakKodeverk;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.PersonPageMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.LamellContainer;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt.OversiktLerret;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.nydialogpanel.NyDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.FortsettDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.RedirectModalWindow;
import org.apache.wicket.ajax.AjaxRequestHandler;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.ArrayList;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.reflect.Reflect.on;
import static no.nav.modig.modia.constants.ModiaConstants.HENT_PERSON_BEGRUNNET;
import static no.nav.modig.modia.events.InternalEvents.*;
import static no.nav.modig.wicket.test.FluentWicketTester.with;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.HENVENDELSEID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.OPPGAVEID;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Temagruppe.ARBD;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService.FikkIkkeTilordnet;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.LamellContainer.LAMELL_MELDINGER;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage.VALGT_OPPGAVE_FNR_ATTR;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage.VALGT_OPPGAVE_ID_ATTR;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.KvitteringsPanel.KVITTERING_VIST;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.LeggTilbakePanel.LEGG_TILBAKE_FERDIG;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {PersonPageMockContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class PersonPageTest extends WicketPageTest {

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    private OppgaveBehandlingService oppgaveBehandlingService;
    @Inject
    private GsakKodeverk gsakKodeverk;

    private final static String testFnr = "12037649749";

    @Before
    public void setUp() {
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
                .should().containComponent(withId("searchPanel").and(ofType(HentPersonPanel.class)))
                .should().containComponent(withId("kjerneinfotabs").and(ofType(VisitkortTabListePanel.class)))
                .should().containComponent(withId("personsokPanel").and(ofType(PersonsokPanel.class)))
                .should().containComponent(withId("lameller").and(ofType(TokenLamellPanel.class)))
                .should().containComponent(withId("nullstill").and(ofType(AbstractLink.class)));
    }

    @Test
    public void viserModaldialVedUlagredeEndringerOgRefresh() {
        wicket.goTo(PersonPage.class, with().param("fnr", testFnr));
        PersonPage personPage = (PersonPage) wicket.tester.getLastRenderedPage();
        RedirectModalWindow redirectPopup = mock(RedirectModalWindow.class);
        LamellContainer lamellContainer = mock(LamellContainer.class);
        on(personPage).setFieldValue("redirectPopup", redirectPopup);
        on(personPage).setFieldValue("lamellContainer", lamellContainer);
        when(lamellContainer.hasUnsavedChanges()).thenReturn(true);

        AjaxRequestTarget target = new AjaxRequestHandler(personPage);
        personPage.refreshKjerneinfo(target, "");

        verify(redirectPopup, times(1)).show(target);
        verify(redirectPopup, times(0)).redirect();
    }

    @Test
    public void viserIkkeModaldialogVedIngenUlagredeEndringerOgRefresh() {
        wicket.goTo(PersonPage.class, with().param("fnr", testFnr));
        PersonPage personPage = (PersonPage) wicket.tester.getLastRenderedPage();
        RedirectModalWindow redirectPopup = mock(RedirectModalWindow.class);
        LamellContainer lamellContainer = mock(LamellContainer.class);
        on(personPage).setFieldValue("redirectPopup", redirectPopup);
        on(personPage).setFieldValue("lamellContainer", lamellContainer);
        when(lamellContainer.hasUnsavedChanges()).thenReturn(false);

        AjaxRequestTarget target = new AjaxRequestHandler(personPage);
        personPage.refreshKjerneinfo(target, "");

        verify(redirectPopup, times(0)).show(target);
        verify(redirectPopup, times(1)).redirect();
    }

    @Test
    public void gittIngenUrlParamVisesNyDialogPanelOgOversiktLamell() {
        wicket.goTo(PersonPage.class, with().param("fnr", testFnr))
                .should().containComponent(ofType(NyDialogPanel.class))
                .should().containComponent(ofType(OversiktLerret.class));
    }

    @Test
    public void gittBareHenvendelseUrlParamVisesMeldingsLamell() {
        wicket.goTo(PersonPage.class, with().param("fnr", testFnr).param(HENVENDELSEID, "id 1"))
                .should().containComponent(withId(LAMELL_MELDINGER));
    }

    @Test
    public void medHenvendelseOgOppgaveUrlParamVisesFortsettDialogPanelOgMeldingLamell() {
        String henvendelsesId = "id 1";

        wicket.goTo(PersonPage.class, with().param("fnr", testFnr).param(HENVENDELSEID, henvendelsesId).param(OPPGAVEID, "oppg1"))
                .should().containComponent(ofType(FortsettDialogPanel.class))
                .should().containComponent(withId(LAMELL_MELDINGER));

        verify(henvendelseUtsendingService).hentTraad(anyString(), eq(henvendelsesId));
    }

    @Test
    public void erstatterNyDialogPanelMedFortsettDialogPanelVedEventetSVAR_PAA_MELDING() {
        wicket.goTo(PersonPage.class, with().param("fnr", testFnr))
                .sendEvent(createEvent(SVAR_PAA_MELDING))
                .should().inAjaxResponse().haveComponents(ofType(FortsettDialogPanel.class));
    }

    @Test
    public void tilordnerIkkeOppgaveIGsakDersomSporsmaaletTidligereErBesvartVedEventetSVAR_PAA_MELDING() throws FikkIkkeTilordnet {
        when(henvendelseUtsendingService.hentTraad(anyString(), anyString())).thenReturn(asList(lagMelding(), lagMelding()));

        wicket.goTo(PersonPage.class, with().param("fnr", testFnr))
                .sendEvent(createEvent(SVAR_PAA_MELDING));

        verify(oppgaveBehandlingService, never()).tilordneOppgaveIGsak(anyString());
    }

    @Test
    public void tilordnerOppgaveIGsakDersomSporsmaaletIkkeTidligereErBesvartVedEventetSVAR_PAA_MELDING() throws FikkIkkeTilordnet {
        Melding spsm = new Melding("id", Meldingstype.SPORSMAL_SKRIFTLIG, DateTime.now());
        spsm.temagruppe = "ARBD";
        when(henvendelseUtsendingService.hentTraad(anyString(), anyString())).thenReturn(asList(spsm));

        wicket.goTo(PersonPage.class, with().param("fnr", testFnr))
                .sendEvent(createEvent(SVAR_PAA_MELDING));

        verify(oppgaveBehandlingService).tilordneOppgaveIGsak(anyString());
    }

    @Test
    public void tilordnerIkkeOppgaveDersomNAVBesvarerEgetSporsmal() throws FikkIkkeTilordnet {
        Melding spsm = new Melding("id", Meldingstype.SPORSMAL_MODIA_UTGAAENDE, DateTime.now());
        spsm.temagruppe = "ARBD";
        when(henvendelseUtsendingService.hentTraad(anyString(), anyString())).thenReturn(asList(spsm));

        wicket.goTo(PersonPage.class, with().param("fnr", testFnr))
                .sendEvent(createEvent(SVAR_PAA_MELDING));

        verify(oppgaveBehandlingService, never()).tilordneOppgaveIGsak(anyString());
    }

    @Test
    public void erstatterDialogPanelMedNyDialogPanelVedRiktigeEvents() {
        assertErstatterDialogPanelMedNyDialogPanelVedEvent(KVITTERING_VIST);
        assertErstatterDialogPanelMedNyDialogPanelVedEvent(LEGG_TILBAKE_FERDIG);
        assertErstatterDialogPanelMedNyDialogPanelVedEvent(Events.SporsmalOgSvar.SVAR_AVBRUTT);
    }

    private void assertErstatterDialogPanelMedNyDialogPanelVedEvent(String event) {
        wicket.goTo(PersonPage.class, with().param("fnr", testFnr))
                .sendEvent(createEvent(event))
                .should().inAjaxResponse().haveComponents(ofType(NyDialogPanel.class));
    }

    @Test
    public void sletterPlukketOppgaveFraSessionVedRiktigeEvents() {
        assertSletterPlukketOppgaveFraSessionVedEvent(MELDING_SENDT_TIL_BRUKER);
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
        wicket.tester.getSession().setAttribute(HENT_PERSON_BEGRUNNET, false);

        wicket.sendEvent(createEvent(FODSELSNUMMER_FUNNET_MED_BEGRUNNElSE, newFnr));

        assertEquals(true, wicket.tester.getSession().getAttribute(HENT_PERSON_BEGRUNNET));
        assertFalse(wicket.tester.ifContains(newFnr).wasFailed());
        assertTrue(wicket.tester.ifContains(testFnr).wasFailed());
    }

    @Test
    public void vellykketGotoHentPersonPageBeggeError() {

        wicket.goTo(PersonPage.class, with().param("fnr", testFnr));

        wicket.sendEvent(createEvent(GOTO_HENT_PERSONPAGE, "{\"errortext\":\"Feil tekst\",\"sikkerhettiltaksbeskrivelse\":\"Farlig.\"}"));
    }

    @Test
    public void vellykketGotoHentPersonPageKunErrortekst() {

        wicket.goTo(PersonPage.class, with().param("fnr", testFnr));

        wicket.sendEvent(createEvent(GOTO_HENT_PERSONPAGE, "{\"errortext\":\"Feil tekst\"}"));
    }

    @Test
    public void shouldExtractSikkerhetstiltaksbeskrivelse() throws JSONException {
        PersonPage page = new PersonPage(new PageParameters());
        String sikkerhetstiltak = page.getSikkerhetsTiltakBeskrivelse("{\"errortext\":\"Feil tekst\",\"sikkerhettiltaksbeskrivelse\":\"Farlig.\"}");
        assertEquals("Farlig.", sikkerhetstiltak);
    }


    @Test
    public void shouldExtractErrortext() throws JSONException {
        PersonPage page = new PersonPage(new PageParameters());
        String errorTxt = page.getErrorText("{\"errortext\":\"Feil tekst\",\"sikkerhettiltaksbeskrivelse\":\"Farlig.\"}");
        assertEquals("Feil tekst", errorTxt);
    }

    @Test
    public void shouldExtractNullWhenFnrtExist() throws JSONException {
        PersonPage page = new PersonPage(new PageParameters());
        String sikkerhetstiltak = page.getSikkerhetsTiltakBeskrivelse("{\"errortext\":\"Feil tekst\"}");
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

}
