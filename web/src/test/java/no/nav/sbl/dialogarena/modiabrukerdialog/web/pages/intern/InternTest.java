package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.kjerneinfo.PersonKjerneinfoPanel;
import no.nav.modig.modia.lamell.TokenLamellPanel;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.modig.wicket.test.internal.Parameters;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.TestSecurityBaseClass;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketApplication;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ApplicationContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.ModiaModalWindow;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.sidebar.SideBar;
import org.apache.wicket.ajax.AjaxRequestHandler;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.mock.MockPageManager;
import org.apache.wicket.protocol.http.WebApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.mockito.Mockito.times;

@ActiveProfiles("test")
@ContextConfiguration(classes = {ApplicationContext.class, WicketTesterConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class InternTest extends TestSecurityBaseClass {

    @Inject
    private FluentWicketTester<?> fluentWicketTester;

    @Test
    public void shouldLoadPage() {
        Parameters param = new Parameters();
        param.pageParameters.set("fnr", "12037649749");
        fluentWicketTester.goTo(Intern.class, param)
                .should().containComponent(withId("searchPanel").and(ofType(HentPersonPanel.class)))
                .should().containComponent(withId("personKjerneinfoPanel").and(ofType(PersonKjerneinfoPanel.class)))
                .should().containComponent(withId("personsokPanel").and(ofType(PersonsokPanel.class)))
                .should().containComponent(withId("lameller").and(ofType(TokenLamellPanel.class)))
                .should().containComponent(withId("sideBar").and(ofType(SideBar.class)))
                .should().containComponent(withId("nullstill").and(ofType(AjaxLink.class)));
    }

    @Test
    public void shouldShowDialogWhenRefreshingKjerneinfoIfThereAreChanges() {
        Parameters param = new Parameters();
        param.pageParameters.set("fnr", "12037649749");
        FluentWicketTester<? extends WebApplication> wicketTester = fluentWicketTester.goTo(Intern.class, param);
        Intern intern = findInternPageInstance(wicketTester);
        ModiaModalWindow modal = Mockito.mock(ModiaModalWindow.class);
        LamellHandler lamellHandler = Mockito.mock(LamellHandler.class);
        Mockito.when(lamellHandler.hasUnsavedChanges()).thenReturn(true);
        Whitebox.setInternalState(intern, "modalWindow", modal);
        Whitebox.setInternalState(intern, "lamellHandler", lamellHandler);
        AjaxRequestTarget target = new AjaxRequestHandler(intern);
        intern.refreshKjerneinfo(target,"");
        Mockito.verify(modal, times(1)).show(target);
        Mockito.verify(modal, times(0)).redirect();

    }

    @Test
    public void shouldNotShowDialogWhenRefreshingKjerneinfoIfThereAreNoChanges() {
        Parameters param = new Parameters();
        param.pageParameters.set("fnr", "12037649749");
        FluentWicketTester<? extends WebApplication> wicketTester = fluentWicketTester.goTo(Intern.class, param);
        Intern intern = findInternPageInstance(wicketTester);
        ModiaModalWindow modal = Mockito.mock(ModiaModalWindow.class);
        LamellHandler lamellHandler = Mockito.mock(LamellHandler.class);
        Mockito.when(lamellHandler.hasUnsavedChanges()).thenReturn(false);
        Whitebox.setInternalState(intern, "modalWindow", modal);
        Whitebox.setInternalState(intern, "lamellHandler", lamellHandler);
        AjaxRequestTarget target = new AjaxRequestHandler(intern);
        intern.refreshKjerneinfo(target,"");
        Mockito.verify(modal, times(0)).show(target);
        Mockito.verify(modal, times(1)).redirect();
    }

    private Intern findInternPageInstance(FluentWicketTester<? extends WebApplication> wicketTester) {
        //graver rundt i private deler av wicket for å finne siden
        WicketApplication application = (WicketApplication) Whitebox.getInternalState(wicketTester, "application");
        MockPageManager pageManager = (MockPageManager) Whitebox.getInternalState(application, "pageManager");
        Intern page = (Intern) pageManager.getPage(0);
        return page;
    }


}
