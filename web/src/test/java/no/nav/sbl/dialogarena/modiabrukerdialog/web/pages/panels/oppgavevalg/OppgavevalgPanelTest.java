package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgavevalg;

import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.BesvareHenvendelsePortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelsePortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.OppgavebehandlingPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SakOgBehandlingPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.services.SoknaderServiceMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.HentPersonPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SykepengerWidgetMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgave.HentOppgavePanel;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.list.ListItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
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
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        HentPersonPanelMockContext.class,
        HenvendelsePortTypeMock.class,
        SykepengerWidgetMockContext.class,
        SakOgBehandlingPortTypeMock.class,
        OppgavebehandlingPortTypeMock.class,
        BesvareHenvendelsePortTypeMock.class,
        SoknaderServiceMock.class
})
public class OppgavevalgPanelTest extends WicketPageTest {

    private static final String OPPGAVEVALG = "oppgavevalg-link";
    private static final String LEGG_TILBAKE = "legg-tilbake-link";
    private static final String LUKK = "lukk";

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
        wicket.tester.executeAjaxEvent(wicket.get()
            .components(ofType(ListItem.class).and(containedInComponent(ofType(HentOppgavePanel.class)))).get(0), "click");
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
