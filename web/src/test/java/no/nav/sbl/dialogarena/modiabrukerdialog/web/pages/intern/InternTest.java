package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.kjerneinfo.PersonKjerneinfoPanel;
import no.nav.modig.modia.lamell.TokenLamellPanel;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.LamellHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.OppgavebehandlingConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.HentPersonPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SykepengerWidgetMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.tjenester.BesvareHenvendelseTjenesteConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.tjenester.HenvendelseinnsynConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.tjenester.SoknaderConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.RedirectModalWindow;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.sidebar.SideBar;
import no.nav.sbl.dialogarena.sporsmalogsvar.besvare.BesvareSporsmalPanel;
import org.apache.wicket.ajax.AjaxRequestHandler;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.lang.reflect.Reflect.on;
import static no.nav.modig.wicket.test.FluentWicketTester.with;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {
        HentPersonPanelMockContext.class,
        WicketTesterConfig.class,
        HenvendelseinnsynConfig.Test.class,
        SykepengerWidgetMockContext.class,
        SoknaderConfig.Test.class,
        OppgavebehandlingConfig.Test.class,
        BesvareHenvendelseTjenesteConfig.Test.class
})
@RunWith(SpringJUnit4ClassRunner.class)
public class InternTest extends WicketPageTest {

    @Test
    public void shouldLoadPage() {
        wicket.goTo(Intern.class, with().param("fnr", "12037649749"))
            .should().containComponent(withId("searchPanel").and(ofType(HentPersonPanel.class)))
            .should().containComponent(withId("personKjerneinfoPanel").and(ofType(PersonKjerneinfoPanel.class)))
            .should().containComponent(withId("personsokPanel").and(ofType(PersonsokPanel.class)))
            .should().containComponent(withId("lameller").and(ofType(TokenLamellPanel.class)))
            .should().containComponent(withId("sideBar").and(ofType(SideBar.class)))
            .should().containComponent(withId("nullstill").and(ofType(AbstractLink.class)));
    }

    @Test
    public void vedUlagredeEndringerOgRefreshSkalViseModaldialog() {
        wicket.goTo(Intern.class, with().param("fnr", "12037649749"));
        Intern intern = (Intern) wicket.tester.getLastRenderedPage();
        RedirectModalWindow redirectPopup = mock(RedirectModalWindow.class);
        LamellHandler lamellHandler = mock(LamellHandler.class);
        on(intern).setFieldValue("redirectPopup", redirectPopup);
        on(intern).setFieldValue("lamellHandler", lamellHandler);
        when(lamellHandler.hasUnsavedChanges()).thenReturn(true);
        AjaxRequestTarget target = new AjaxRequestHandler(intern);
        intern.refreshKjerneinfo(target, "");
        verify(redirectPopup, times(1)).show(target);
        verify(redirectPopup, times(0)).redirect();
    }

    @Test
    public void vedIngenUlagredeEndringerOgRefreshSkalIkkeViseModaldialog() {
        wicket.goTo(Intern.class, with().param("fnr", "12037649749"));
        Intern intern = (Intern) wicket.tester.getLastRenderedPage();
        RedirectModalWindow redirectPopup = mock(RedirectModalWindow.class);
        LamellHandler lamellHandler = mock(LamellHandler.class);
        on(intern).setFieldValue("redirectPopup", redirectPopup);
        on(intern).setFieldValue("lamellHandler", lamellHandler);
        when(lamellHandler.hasUnsavedChanges()).thenReturn(false);
        AjaxRequestTarget target = new AjaxRequestHandler(intern);
        intern.refreshKjerneinfo(target, "");
        verify(redirectPopup, times(0)).show(target);
        verify(redirectPopup, times(1)).redirect();
    }

    @Test
    public void besvareSporsmalPanelErSynligNaarOppgaveIdGisIUrl() {
        wicket
            .goTo(InternBesvaremodus.class, with().param("fnr", "12037649749").param("oppgaveId", "123"))
            .should().containComponent(ofType(BesvareSporsmalPanel.class).and(thatIsVisible()));
    }

}
