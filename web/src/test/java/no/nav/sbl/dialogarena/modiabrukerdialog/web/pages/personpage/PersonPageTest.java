package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.kjerneinfo.PersonKjerneinfoPanel;
import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.modig.modia.lamell.TokenLamellPanel;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.AktoerPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.BehandleHenvendelsePortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.BehandleJournalV2PortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GosysNavAnsattPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakHentSakslisteMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakOppgaveV2PortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakOppgavebehandlingV2PortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelsePortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelseSoknaderPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SakOgBehandlingPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SendUtHenvendelsePortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.UtbetalingPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.KjerneinfoPepMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.PersonPageMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SaksbehandlerInstillingerPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SykepengerWidgetMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.LamellContainer;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.RedirectModalWindow;
import no.nav.sbl.dialogarena.sporsmalogsvar.context.SporsmalOgSvarContext;
import no.nav.sbl.dialogarena.utbetaling.lamell.context.UtbetalingLamellContext;
import org.apache.wicket.ajax.AjaxRequestHandler;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.lang.reflect.Reflect.on;
import static no.nav.modig.wicket.test.FluentWicketTester.with;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {
        PersonPageMockContext.class,
        KjerneinfoPepMockContext.class,
        SykepengerWidgetMockContext.class,
        AktoerPortTypeMock.class,
        UtbetalingLamellContext.class,
        SakOgBehandlingPortTypeMock.class,
        HenvendelseSoknaderPortTypeMock.class,
        SporsmalOgSvarContext.class,
        UtbetalingPortTypeMock.class,
        HenvendelsePortTypeMock.class,
        SendUtHenvendelsePortTypeMock.class,
        BehandleHenvendelsePortTypeMock.class,
        GsakHentSakslisteMock.class,
        BehandleJournalV2PortTypeMock.class,
        GsakOppgaveV2PortTypeMock.class,
        GsakOppgavebehandlingV2PortTypeMock.class,
        GosysNavAnsattPortTypeMock.class,
        SaksbehandlerInstillingerPanelMockContext.class
})
@RunWith(SpringJUnit4ClassRunner.class)
public class PersonPageTest extends WicketPageTest {

    @Override
    protected void additionalSetup() {
        System.setProperty(StaticSubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
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
}
