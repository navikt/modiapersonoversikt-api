package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.kjerneinfo.PersonKjerneinfoPanel;
import no.nav.modig.modia.lamell.TokenLamellPanel;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.test.EventGenerator;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.SvarEllerReferat;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.PersonPageMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.LamellContainer;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.RedirectModalWindow;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.referatpanel.ReferatPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.SvarPanel;
import org.apache.wicket.ajax.AjaxRequestHandler;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;

import static no.nav.modig.modia.constants.ModiaConstants.HENT_PERSON_BEGRUNNET;
import static no.nav.modig.lang.reflect.Reflect.on;
import static no.nav.modig.modia.events.InternalEvents.FODSELSNUMMER_FUNNET_MED_BEGRUNNElSE;
import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.modig.modia.events.InternalEvents.SVAR_PAA_MELDING;
import static no.nav.modig.wicket.test.FluentWicketTester.with;
import static no.nav.modig.wicket.test.matcher.CombinableMatcher.both;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage.OPPGAVEID;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage.SVAR_OG_REFERAT_PANEL_ID;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage.VALGT_OPPGAVE_FNR_ATTR;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage.VALGT_OPPGAVE_ID_ATTR;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.Temagruppe.ARBD;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.LeggTilbakePanel.LEGG_TILBAKE_UTFORT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.SvarPanel.SVAR_AVBRUTT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {PersonPageMockContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class PersonPageTest extends WicketPageTest {

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    private OppgaveBehandlingService oppgaveBehandlingService;

    private final static String testFnr = "12037649749";

    @Before
    public void setUp() {
        Sporsmal sporsmal = new Sporsmal("id", DateTime.now());
        sporsmal.temagruppe = ARBD.name();
        sporsmal.oppgaveId = "id";
        when(henvendelseUtsendingService.getSporsmalFromOppgaveId(anyString(), anyString())).thenReturn(sporsmal);
        when(henvendelseUtsendingService.getSporsmal(anyString())).thenReturn(sporsmal);
    }

    @Test
    public void shouldLoadPage() {
        wicket.goTo(PersonPage.class, with().param("fnr", testFnr))
                .should().containComponent(withId("searchPanel").and(ofType(HentPersonPanel.class)))
                .should().containComponent(withId("personKjerneinfoPanel").and(ofType(PersonKjerneinfoPanel.class)))
                .should().containComponent(withId("personsokPanel").and(ofType(PersonsokPanel.class)))
                .should().containComponent(withId("lameller").and(ofType(TokenLamellPanel.class)))
                .should().containComponent(withId("nullstill").and(ofType(AbstractLink.class)));
    }

    @Test
    public void vedUlagredeEndringerOgRefreshSkalViseModaldialog() {
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
    public void vedIngenUlagredeEndringerOgRefreshSkalIkkeViseModaldialog() {
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
    public void skalViseReferatPanelSomDefaultSvarOfReferatPanel() {
        wicket.goTo(PersonPage.class, with().param("fnr", testFnr))
                .should().containComponent(both(withId(SVAR_OG_REFERAT_PANEL_ID)).and(ofType(ReferatPanel.class)));
    }

    @Test
    public void skalErstatteReferatPanelMedSvarPanelDersomOppgaveidErSattIPageParameters() {
        String oppgaveid = "oppgaveid";
        wicket.goTo(PersonPage.class, with().param("fnr", testFnr).param(OPPGAVEID, oppgaveid))
                .should().containComponent(both(withId(SVAR_OG_REFERAT_PANEL_ID)).and(ofType(SvarPanel.class)));

        verify(henvendelseUtsendingService).getSporsmalFromOppgaveId(testFnr, oppgaveid);
    }

    @Test
    public void skalIkkeErstatteReferatPanelMedSvarPanelDersomHenvendelseIdErSattIPageParameters() {
        String henvendelsesId = "id 1";
        wicket.goTo(PersonPage.class, with().param("fnr", testFnr).param(PersonPage.HENVENDELSEID, henvendelsesId))
                .should().containComponent(both(withId(SVAR_OG_REFERAT_PANEL_ID)).and(ofType(ReferatPanel.class)));
    }

    @Test
    public void skalErstatteReferatPanelMedSvarPanelDersomHenvendelseIdOgOppgaveIdErSattIPageParameters() {
        String henvendelsesId = "id 1";
        wicket.goTo(PersonPage.class, with().param("fnr", testFnr).param(PersonPage.HENVENDELSEID, henvendelsesId).param(OPPGAVEID, "oppgaveid"))
                .should().containComponent(both(withId(SVAR_OG_REFERAT_PANEL_ID)).and(ofType(SvarPanel.class)));

        verify(henvendelseUtsendingService).getSporsmal(henvendelsesId);
        verify(henvendelseUtsendingService).getSvarEllerReferatForSporsmal(testFnr, henvendelsesId);
    }

    @Test
    public void skalErstatteReferatPanelMedSvarPanelVedEventetSVAR_PAA_MELDING() {
        when(henvendelseUtsendingService.getSvarEllerReferatForSporsmal(anyString(), anyString())).thenReturn(new ArrayList<>(Arrays.asList(new SvarEllerReferat())));

        wicket.goTo(PersonPage.class, with().param("fnr", testFnr))
                .sendEvent(createEvent(SVAR_PAA_MELDING))
                .should().inAjaxResponse().haveComponents(ofType(SvarPanel.class));
    }

    @Test
    public void skalIkkeTilordneOppgaveIGsakDersomSporsmaaletTidligereErBesvartVedEventetSVAR_PAA_MELDING() {
        when(henvendelseUtsendingService.getSvarEllerReferatForSporsmal(anyString(), anyString())).thenReturn(new ArrayList<>(Arrays.asList(new SvarEllerReferat())));

        wicket.goTo(PersonPage.class, with().param("fnr", testFnr))
                .sendEvent(createEvent(SVAR_PAA_MELDING));

        verify(oppgaveBehandlingService, never()).tilordneOppgaveIGsak(anyString());
    }

    @Test
    public void skalTilordneOppgaveIGsakDersomSporsmaaletIkkeTidligereErBesvartVedEventetSVAR_PAA_MELDING() {
        when(henvendelseUtsendingService.getSvarEllerReferatForSporsmal(anyString(), anyString())).thenReturn(new ArrayList<SvarEllerReferat>());

        wicket.goTo(PersonPage.class, with().param("fnr", testFnr))
                .sendEvent(createEvent(SVAR_PAA_MELDING));

        verify(oppgaveBehandlingService).tilordneOppgaveIGsak(anyString());
    }

    @Test
    public void skalErstatteSvarOgReferatPanelMedReferatPanelVedRiktigeEvents() {
        assertErstatterSvarOgReferatPanelMedReferatPanelVedEvent(MELDING_SENDT_TIL_BRUKER);
        assertErstatterSvarOgReferatPanelMedReferatPanelVedEvent(LEGG_TILBAKE_UTFORT);
        assertErstatterSvarOgReferatPanelMedReferatPanelVedEvent(SVAR_AVBRUTT);
    }

    private void assertErstatterSvarOgReferatPanelMedReferatPanelVedEvent(String event) {
        wicket.goTo(PersonPage.class, with().param("fnr", testFnr))
                .sendEvent(createEvent(event))
                .should().inAjaxResponse().haveComponents(ofType(ReferatPanel.class));
    }

    @Test
    public void skalSlettePlukketOppgaveFraSessionVedRiktigeEvents() {
        assertSletterPlukketOppgaveFraSessionVedEvent(MELDING_SENDT_TIL_BRUKER);
        assertSletterPlukketOppgaveFraSessionVedEvent(LEGG_TILBAKE_UTFORT);
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
    public void skalOppdatereKjerneInfoVedFodselsnummerFunnetMedBegrunnelse() {
        final String newFnr = "12345612345";

        wicket.goTo(PersonPage.class, with().param("fnr", testFnr));
        wicket.tester.getSession().setAttribute(HENT_PERSON_BEGRUNNET, false);
        wicket.sendEvent(createEvent(FODSELSNUMMER_FUNNET_MED_BEGRUNNElSE, newFnr));

        assertEquals(true, wicket.tester.getSession().getAttribute(HENT_PERSON_BEGRUNNET));
        assertFalse(wicket.tester.ifContains(newFnr).wasFailed());
        assertTrue(wicket.tester.ifContains(testFnr).wasFailed());
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

}
