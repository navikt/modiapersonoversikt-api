package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgavevalg;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.OppgavebehandlingConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.felles.HenvendelseinnsynConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.felles.SoknaderConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.BesvareHenvendelseMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.HentPersonPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.KjerneinfoPepMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SykepengerWidgetMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgave.HentOppgavePanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.BesvareServiceConfig;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.list.ListItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.wicket.test.matcher.CombinableMatcher.both;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.containedInComponent;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsInvisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        HentPersonPanelMockContext.class,
        KjerneinfoPepMockContext.class,
        WicketTesterConfig.class,
        HenvendelseinnsynConfig.Test.class,
        SykepengerWidgetMockContext.class,
        SoknaderConfig.Test.class,
        OppgavebehandlingConfig.Test.class,
        BesvareServiceConfig.Default.class,
        BesvareHenvendelseMockContext.class
})
public class OppgavevalgPanelTest {

    @Inject
    private FluentWicketTester<?> wicket;

    private static final String OPPGAVEVALG = "oppgavevalg-link";
    private static final String LEGG_TILBAKE = "legg-tilbake-link";
    private static final String LUKK = "lukk";

    private static boolean harValgtTema = false;

    @Test
    public void skalIkkeHaOppgavevalgNaarManIkkeHarPlukketOppgave() {
        Component oppgavevalg = wicket.goTo(Intern.class).get().component(ofType(OppgavevalgPanel.class));
        assertThat(oppgavevalg, thatIsInvisible());
    }

    @Test
    public void skalHaOppgavevalgNaarManHarPlukketOppgave() {
        plukkOppgave();
        assertThat(wicket.get().component(ofType(OppgavevalgPanel.class)), thatIsVisible());
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
        assertThat(wicket.get().component(ofType(LeggTilbakeForm.class)), thatIsVisible());
    }

    @Test
    public void skalLukkeFormNaarManKlikkerPaaLukk() {
        plukkOppgave();
        trykkPaa(OPPGAVEVALG);
        trykkPaa(LEGG_TILBAKE);
        trykkPaa(LUKK);
        assertThat(wicket.get().component(ofType(LeggTilbakeForm.class)), thatIsInvisible());
    }

    @Test
    public void defaultSkalVaereInhabil() {
        plukkOppgave();
        trykkPaa(OPPGAVEVALG);
        trykkPaa(LEGG_TILBAKE);
        assertThat(((AarsakVM) wicket.get().component(ofType(LeggTilbakeForm.class)).getDefaultModelObject()).getValg().getTekst(),
                equalTo("Jeg er inhabil"));
    }

    private void plukkOppgave() {
        wicket.goTo(Intern.class).click().link(withId("plukk-oppgave"));
        if (!harValgtTema) {
            wicket.tester.executeAjaxEvent(wicket.get()
                    .components(ofType(ListItem.class).and(containedInComponent(ofType(HentOppgavePanel.class)))).get(0), "click");
            harValgtTema = true;
        }
    }

    private void trykkPaa(String id) {
        wicket.tester.executeAjaxEvent(wicket.get()
                .component(both(containedInComponent(ofType(OppgavevalgPanel.class)))
                        .and(withId(id))), "click");
    }

    private Component oppgavevalgCompenentWithId(String id) {
        return wicket.get().component(both(containedInComponent(ofType(OppgavevalgPanel.class))).and(withId(id)));
    }

}
