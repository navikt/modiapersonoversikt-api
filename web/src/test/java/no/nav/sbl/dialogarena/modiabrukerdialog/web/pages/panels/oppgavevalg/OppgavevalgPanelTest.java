package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgavevalg;

import javax.inject.Inject;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.TestSecurityBaseClass;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ApplicationContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgave.HentOppgavePanel;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.list.ListItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.containedInComponent;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationContext.class, WicketTesterConfig.class})
public class OppgavevalgPanelTest extends TestSecurityBaseClass {

    @Inject
    private FluentWicketTester<?> fluentWicketTester;

    @Test
    public void skalIkkeHaOppgavevalgNaarManIkkeHarPlukketOppgave() {
        Component oppgavevalg = fluentWicketTester.goTo(Intern.class).get().component(ofType(OppgavevalgPanel.class));
        assertFalse(oppgavevalg.isVisibleInHierarchy());
    }

    @Test
    public void skalHaOppgavevalgNaarManHarPlukketOppgave() {
        plukkOppgave();
        assertTrue(fluentWicketTester.get().component(ofType(OppgavevalgPanel.class)).isVisibleInHierarchy());
    }

    private void plukkOppgave() {
        fluentWicketTester.goTo(Intern.class).click().link(withId("plukk-oppgave"));
        fluentWicketTester.tester.executeAjaxEvent(fluentWicketTester.get().components(ofType(ListItem.class).and(containedInComponent(ofType(HentOppgavePanel.class)))).get(0), "click");
    }

}
