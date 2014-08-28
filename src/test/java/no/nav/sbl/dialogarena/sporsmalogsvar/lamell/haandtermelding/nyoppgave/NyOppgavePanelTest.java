package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgave;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
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
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SPORSMAL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMelding;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class NyOppgavePanelTest extends WicketPageTest {

    private Melding melding;
    private InnboksVM innboksVM = mock(InnboksVM.class);

    @Before
    public void setUp() {
        TraadVM traadVM = mock(TraadVM.class);
        String eldsteMeldingId = "id";
        melding = createMelding(eldsteMeldingId, SPORSMAL, now(), "temagruppe", eldsteMeldingId);
        MeldingVM meldingVM = new MeldingVM(melding, 1);
        when(innboksVM.getValgtTraad()).thenReturn(traadVM);
        when(traadVM.getEldsteMelding()).thenReturn(meldingVM);
    }

    @Test
    public void skalKjoereOppNyOppgavePanelMedRiktigeFelterOgTyper() {
        wicket.goToPageWith(new TestNyOppgavePanel("panel", innboksVM))
                .should().containLabelsSaying(melding.temagruppe)
                .should().containComponent(both(ofType(Form.class).and(withId("nyoppgaveform"))))
                .should().containComponent(both(ofType(FeedbackPanel.class).and(withId("feedback"))))
                .should().containComponent(both(ofType(AjaxButton.class).and(withId("opprettoppgave"))))
                .should().containComponent(both(ofType(AjaxLink.class).and(withId("avbryt"))));
    }

    @Test
    public void skalLukkePaneletIdetManAvbryter() {
        TestNyOppgavePanel nyOppgavePanel = new TestNyOppgavePanel("panel", innboksVM);
        wicket.goToPageWith(nyOppgavePanel)
                .should().containComponent(thatIsVisible().and(withId("panel")))
                .click().link(withId("avbryt"));

        assertThat(nyOppgavePanel.isVisibilityAllowed(), is(false));
    }
}
