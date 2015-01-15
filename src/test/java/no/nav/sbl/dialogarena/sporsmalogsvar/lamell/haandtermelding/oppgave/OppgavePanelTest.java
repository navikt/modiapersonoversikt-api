package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.oppgave;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.*;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.wicket.test.matcher.CombinableMatcher.both;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMelding;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class OppgavePanelTest extends WicketPageTest {

    private Melding melding;
    private InnboksVM innboksVM = mock(InnboksVM.class);

    @Before
    public void setUp() {
        TraadVM traadVM = mock(TraadVM.class);
        String eldsteMeldingId = "id";
        melding = createMelding(eldsteMeldingId, SPORSMAL_SKRIFTLIG, now(), "temagruppe", eldsteMeldingId);
        MeldingVM meldingVM = new MeldingVM(melding, 1);
        when(innboksVM.getValgtTraad()).thenReturn(traadVM);
        when(traadVM.getEldsteMelding()).thenReturn(meldingVM);
    }

    @Test
    public void skalKjoereOppNyOppgavePanelMedRiktigeFelterOgTyper() {
        wicket.goToPageWith(new TestOppgavePanel("panel", innboksVM))
                .should().containLabelsSaying(melding.temagruppe)
                .should().containComponent(both(ofType(Form.class).and(withId("nyoppgaveform"))))
                .should().containComponent(both(ofType(FeedbackPanel.class).and(withId("feedback"))))
                .should().containComponent(both(ofType(AjaxButton.class).and(withId("opprettoppgave"))))
                .should().containComponent(both(ofType(AjaxLink.class).and(withId("avbryt"))));
    }

    @Test
    public void skalLukkePaneletIdetManAvbryter() {
        TestOppgavePanel nyOppgavePanel = new TestOppgavePanel("panel", innboksVM);
        wicket.goToPageWith(nyOppgavePanel)
                .should().containComponent(thatIsVisible().and(withId("panel")))
                .click().link(withId("avbryt"));

        assertThat(nyOppgavePanel.isVisibilityAllowed(), is(false));
    }
}
