package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.kjerneinfo.PersonKjerneinfoPanel;
import no.nav.modig.modia.lamell.TokenLamellPanel;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.modig.wicket.test.internal.Parameters;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.TestSecurityBaseClass;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ApplicationContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.ModiaModalWindow;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.sidebar.SideBar;
import org.apache.wicket.ajax.AjaxRequestHandler;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.common.MDCOperations.MDC_CALL_ID;
import static no.nav.modig.common.MDCOperations.generateCallId;
import static no.nav.modig.common.MDCOperations.putToMDC;
import static no.nav.modig.wicket.test.FluentWicketTester.with;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

@ActiveProfiles({"test"})
@ContextConfiguration(classes = {ApplicationContext.class, WicketTesterConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class InternTest extends TestSecurityBaseClass {

    @Inject
    private FluentWicketTester<?> wicket;

    @Before
    public void setupMDC() {
        putToMDC(MDC_CALL_ID, generateCallId());
    }

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
    public void shouldShowDialogWhenRefreshingKjerneinfoIfThereAreChanges() {
        Parameters param = new Parameters();
        param.pageParameters.set("fnr", "12037649749");
        wicket.goTo(Intern.class, param);
        Intern intern = (Intern) wicket.tester.getLastRenderedPage();
        ModiaModalWindow modal = mock(ModiaModalWindow.class);
        LamellHandler lamellHandler = mock(LamellHandler.class);
        when(lamellHandler.hasUnsavedChanges()).thenReturn(true);
        setInternalState(intern, "modalWindow", modal);
        setInternalState(intern, "lamellHandler", lamellHandler);
        AjaxRequestTarget target = new AjaxRequestHandler(intern);
        intern.refreshKjerneinfo(target, "");
        verify(modal, times(1)).show(target);
        verify(modal, times(0)).redirect();

    }

    @Test
    public void shouldNotShowDialogWhenRefreshingKjerneinfoIfThereAreNoChanges() {
        Parameters param = new Parameters();
        param.pageParameters.set("fnr", "12037649749");
        wicket.goTo(Intern.class, param);
        Intern intern = (Intern) wicket.tester.getLastRenderedPage();
        ModiaModalWindow modal = mock(ModiaModalWindow.class);
        LamellHandler lamellHandler = mock(LamellHandler.class);
        when(lamellHandler.hasUnsavedChanges()).thenReturn(false);
        setInternalState(intern, "modalWindow", modal);
        setInternalState(intern, "lamellHandler", lamellHandler);
        AjaxRequestTarget target = new AjaxRequestHandler(intern);
        intern.refreshKjerneinfo(target, "");
        verify(modal, times(0)).show(target);
        verify(modal, times(1)).redirect();
    }


}
