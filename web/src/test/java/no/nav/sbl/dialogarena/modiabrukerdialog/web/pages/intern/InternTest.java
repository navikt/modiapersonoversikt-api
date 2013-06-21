package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.kjerneinfo.PersonKjerneinfoPanel;
import no.nav.modig.modia.lamell.TokenLamellPanel;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.TestSecurityBaseClass;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ApplicationContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.sidebar.SideBar;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;

@ContextConfiguration(classes = {ApplicationContext.class, WicketTesterConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class InternTest extends TestSecurityBaseClass {


    @Inject
    private FluentWicketTester fluentWicketTester;


    @Test
    public void shouldLoadPage(){
        fluentWicketTester.goTo(Intern.class).should().containComponent(withId("searchPanel").and(ofType(HentPersonPanel.class)))
                .should().containComponent(withId("personKjerneinfoPanel").and(ofType(PersonKjerneinfoPanel.class)))
                .should().containComponent(withId("personsokPanel").and(ofType(PersonsokPanel.class)))
                .should().containComponent(withId("lameller").and(ofType(TokenLamellPanel.class)))
                .should().containComponent(withId("sideBar").and(ofType(SideBar.class)))
                .should().containComponent(withId("nullstill").and(ofType(AjaxLink.class)));
    }


}
