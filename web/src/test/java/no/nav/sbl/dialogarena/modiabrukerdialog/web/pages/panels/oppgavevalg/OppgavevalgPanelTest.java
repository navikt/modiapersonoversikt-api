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

import static no.nav.modig.wicket.test.matcher.CombinableMatcher.both;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.containedInComponent;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsInvisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationContext.class, WicketTesterConfig.class})
public class OppgavevalgPanelTest extends TestSecurityBaseClass {

    @Inject
    private FluentWicketTester<?> fluentWicketTester;

    private static final String OPPGAVEVALG = "oppgavevalg-link";
    private static final String LEGG_TILBAKE = "legg-tilbake-link";
    private static final String LUKK = "lukk";

    private static boolean harValgtTema = false;

    @Test
    public void skalIkkeHaOppgavevalgNaarManIkkeHarPlukketOppgave() {
        Component oppgavevalg = fluentWicketTester.goTo(Intern.class).get().component(ofType(OppgavevalgPanel.class));
        assertThat(oppgavevalg, thatIsInvisible());
    }

    @Test
    public void skalHaOppgavevalgNaarManHarPlukketOppgave() {
        plukkOppgave();
        assertThat(fluentWicketTester.get().component(ofType(OppgavevalgPanel.class)), thatIsVisible());
    }

    @Test
    public void skalFaaOppAlleValgNarManTrykkerPaaOppgavevalg() {
        plukkOppgave();
        trykkPaa(OPPGAVEVALG);
        assertThat(oppgavevalgCompenentWithId(LEGG_TILBAKE), thatIsVisible());
    }

    @Test
    public void skalToggleVisningAvValg() {
        plukkOppgave();
        trykkPaa(OPPGAVEVALG);
        assertThat(oppgavevalgCompenentWithId(LEGG_TILBAKE), thatIsVisible());
        trykkPaa(OPPGAVEVALG);
        assertThat(oppgavevalgCompenentWithId(LEGG_TILBAKE), thatIsInvisible());
    }

    @Test
    public void skalViseFormNaarManVelgerLeggTilbake() {
        plukkOppgave();
        trykkPaa(OPPGAVEVALG);
        trykkPaa(LEGG_TILBAKE);
        assertThat(fluentWicketTester.get().component(ofType(LeggTilbakeForm.class)), thatIsVisible());
    }

    @Test
    public void skalLukkeFormNaarManKlikkerPaaLukk() {
        plukkOppgave();
        trykkPaa(OPPGAVEVALG);
        trykkPaa(LEGG_TILBAKE);
        trykkPaa(LUKK);
        assertThat(fluentWicketTester.get().component(ofType(LeggTilbakeForm.class)), thatIsInvisible());
    }

    @Test
    public void defaultSkalVaereInhabil() {
        plukkOppgave();
        trykkPaa(OPPGAVEVALG);
        trykkPaa(LEGG_TILBAKE);
        assertThat(((AarsakVM) fluentWicketTester.get().component(ofType(LeggTilbakeForm.class)).getDefaultModelObject()).getValg().getTekst(),
                equalTo("Jeg er inhabil"));
    }

    private void plukkOppgave() {
        fluentWicketTester.goTo(Intern.class).click().link(withId("plukk-oppgave"));
        if (!harValgtTema) {
            fluentWicketTester.tester.executeAjaxEvent(fluentWicketTester.get()
                    .components(ofType(ListItem.class).and(containedInComponent(ofType(HentOppgavePanel.class)))).get(0), "click");
            harValgtTema = true;
        }
    }

    private void trykkPaa(String id) {
        fluentWicketTester.tester.executeAjaxEvent(fluentWicketTester.get()
                .component(both(containedInComponent(ofType(OppgavevalgPanel.class)))
                        .and(withId(id))), "click");
    }

    private Component oppgavevalgCompenentWithId(String id) {
        return fluentWicketTester.get().component(both(containedInComponent(ofType(OppgavevalgPanel.class))).and(withId(id)));
    }

}
