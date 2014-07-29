package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.kjerneinfo.PersonKjerneinfoPanel;
import no.nav.modig.modia.lamell.TokenLamellPanel;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.test.EventGenerator;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Svar;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SakService;
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

import static no.nav.modig.lang.reflect.Reflect.on;
import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.modig.modia.events.InternalEvents.SVAR_PAA_MELDING;
import static no.nav.modig.wicket.test.FluentWicketTester.with;
import static no.nav.modig.wicket.test.matcher.CombinableMatcher.both;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage.OPPGAVEID;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage.SVAR_OG_REFERAT_PANEL_ID;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.Temagruppe.ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.LeggTilbakePanel.LEGG_TILBAKE_UTFORT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.SvarPanel.SVAR_AVBRUTT;
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
    private SakService sakService;

    @Before
    public void setUp() {
        Sporsmal sporsmal = new Sporsmal("id", DateTime.now());
        sporsmal.temagruppe = ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT.name();
        sporsmal.oppgaveId = "id";
        when(sakService.getSporsmalFromOppgaveId(anyString(), anyString())).thenReturn(sporsmal);
        when(sakService.getSporsmal(anyString())).thenReturn(sporsmal);
    }

    @Test
    public void shouldLoadPage() {
        wicket.goTo(PersonPage.class, with().param("fnr", "12037649749"))
                .should().containComponent(withId("searchPanel").and(ofType(HentPersonPanel.class)))
                .should().containComponent(withId("personKjerneinfoPanel").and(ofType(PersonKjerneinfoPanel.class)))
                .should().containComponent(withId("personsokPanel").and(ofType(PersonsokPanel.class)))
                .should().containComponent(withId("lameller").and(ofType(TokenLamellPanel.class)))
                .should().containComponent(withId("nullstill").and(ofType(AbstractLink.class)));
    }

    @Test
    public void vedUlagredeEndringerOgRefreshSkalViseModaldialog() {
        wicket.goTo(PersonPage.class, with().param("fnr", "12037649749"));
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
        wicket.goTo(PersonPage.class, with().param("fnr", "12037649749"));
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
        wicket.goTo(PersonPage.class, with().param("fnr", "12037649749"))
                .should().containComponent(both(withId(SVAR_OG_REFERAT_PANEL_ID)).and(ofType(ReferatPanel.class)));
    }

    @Test
    public void skalErstatteReferatPanelMedSvarPanelDersomOppgaveidErSattIPageParameters() {
        wicket.goTo(PersonPage.class, with().param("fnr", "12037649749").param(OPPGAVEID, "oppgaveid"))
                .should().containComponent(both(withId(SVAR_OG_REFERAT_PANEL_ID)).and(ofType(SvarPanel.class)));
    }

    @Test
    public void skalErstatteReferatPanelMedSvarPanelVedEventetSVAR_PAA_MELDING() {
        when(sakService.getSvarTilSporsmal(anyString(), anyString())).thenReturn(new ArrayList<>(Arrays.asList(new Svar())));

        wicket.goTo(PersonPage.class, with().param("fnr", "12037649749"))
                .sendEvent(createEvent(SVAR_PAA_MELDING))
                .should().inAjaxResponse().haveComponents(ofType(SvarPanel.class));
    }

    @Test
    public void skalIkkeTilordneOppgaveIGsakDersomSporsmaaletTidligereErBesvartVedEventetSVAR_PAA_MELDING() {
        when(sakService.getSvarTilSporsmal(anyString(), anyString())).thenReturn(new ArrayList<>(Arrays.asList(new Svar())));

        wicket.goTo(PersonPage.class, with().param("fnr", "12037649749"))
                .sendEvent(createEvent(SVAR_PAA_MELDING));

        verify(sakService, never()).tilordneOppgaveIGsak(anyString());
    }

    @Test
    public void skalTilordneOppgaveIGsakDersomSporsmaaletIkkeTidligereErBesvartVedEventetSVAR_PAA_MELDING() {
        when(sakService.getSvarTilSporsmal(anyString(), anyString())).thenReturn(new ArrayList<Svar>());

        wicket.goTo(PersonPage.class, with().param("fnr", "12037649749"))
                .sendEvent(createEvent(SVAR_PAA_MELDING));

        verify(sakService).tilordneOppgaveIGsak(anyString());
    }

    @Test
    public void skalErstatteSvarOgReferatPanelMedReferatPanelVedEventetMELDING_SENDT_TIL_BRUKER() {
        wicket.goTo(PersonPage.class, with().param("fnr", "12037649749"))
                .sendEvent(createEvent(MELDING_SENDT_TIL_BRUKER))
                .should().inAjaxResponse().haveComponents(ofType(ReferatPanel.class));
    }

    @Test
    public void skalErstatteSvarOgReferatPanelMedReferatPanelVedEventetLEGG_TILBAKE_UTFORT() {
        wicket.goTo(PersonPage.class, with().param("fnr", "12037649749"))
                .sendEvent(createEvent(LEGG_TILBAKE_UTFORT))
                .should().inAjaxResponse().haveComponents(ofType(ReferatPanel.class));
    }

    @Test
    public void skalErstatteSvarOgReferatPanelMedReferatPanelVedEventetSVAR_AVBRUTT() {
        wicket.goTo(PersonPage.class, with().param("fnr", "12037649749"))
                .sendEvent(createEvent(SVAR_AVBRUTT))
                .should().inAjaxResponse().haveComponents(ofType(ReferatPanel.class));
    }

    private EventGenerator createEvent(final String eventNavn) {
        return new EventGenerator() {
            @Override
            public Object createEvent(AjaxRequestTarget target) {
                return new NamedEventPayload(eventNavn, "");
            }
        };
    }

}
