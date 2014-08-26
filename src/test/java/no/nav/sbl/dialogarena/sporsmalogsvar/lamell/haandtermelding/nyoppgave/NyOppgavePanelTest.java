package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgave;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.NyOppgave;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Captor;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.wicket.test.matcher.CombinableMatcher.both;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;

import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SPORSMAL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMelding;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class NyOppgavePanelTest extends WicketPageTest {

    @Captor
    private ArgumentCaptor<NyOppgave> nyOppgaveArgumentCaptor;

    @Inject
    private GsakService gsakService;

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
                .should().containComponent(both(ofType(AjaxLink.class).and(withId("avbrytlink"))));
    }

    @Test
    public void skalGiFeilmeldingHvisManSenderInnSkjemaUtenAAFylleInnAlleFeltene() {
        TestNyOppgavePanel nyOppgavePanel = new TestNyOppgavePanel("panel", innboksVM);
        wicket.goToPageWith(nyOppgavePanel)
                .inForm("panel:nyoppgave-form:nyoppgaveform")
                .submit();

        List<String> errorMessages = wicket.get().errorMessages();
        assertTrue(errorMessages.contains(nyOppgavePanel.get("nyoppgave-form:nyoppgaveform").getString("nyoppgaveform.tema.Required")));
        assertTrue(errorMessages.contains(nyOppgavePanel.get("nyoppgave-form:nyoppgaveform").getString("enhet.Required")));
        assertTrue(errorMessages.contains(nyOppgavePanel.get("nyoppgave-form:nyoppgaveform").getString("type.Required")));
        assertTrue(errorMessages.contains(nyOppgavePanel.get("nyoppgave-form:nyoppgaveform").getString("prioritet.Required")));
        assertTrue(errorMessages.contains(nyOppgavePanel.get("nyoppgave-form:nyoppgaveform").getString("beskrivelse.Required")));
    }

    @Test
    public void skalLukkePaneletIdetManAvbryter() {
        TestNyOppgavePanel nyOppgavePanel = new TestNyOppgavePanel("panel", innboksVM);
        wicket.goToPageWith(nyOppgavePanel)
                .should().containComponent(thatIsVisible().and(withId("panel")))
                .click().link(withId("avbrytlink"));

        assertThat(nyOppgavePanel.isVisibilityAllowed(), is(false));
    }

    @Test
    public void skalSendeNyOppgaveObjektetTilGsakTjenestenForAaOppretteNy() {
        String beskrivelse = "Dette er en beskrivelse";
        TestNyOppgavePanel nyOppgavePanel = new TestNyOppgavePanel("panel", innboksVM);
        MockitoAnnotations.initMocks(this);
        wicket.goToPageWith(nyOppgavePanel)
                .inForm("panel:nyoppgave-form:nyoppgaveform")
                    .select("tema", 0)
                    .select("enhet", 0)
                    .select("type", 0)
                    .select("prioritet", 0)
                    .write("beskrivelse", beskrivelse)
                    .submitWithAjaxButton(withId("opprettoppgave"));

        verify(gsakService).opprettGsakOppgave(nyOppgaveArgumentCaptor.capture());
        NyOppgave nyOppgave = nyOppgaveArgumentCaptor.getValue();
        assertThat(nyOppgave.beskrivelse, is(beskrivelse));
        assertThat(nyOppgave.henvendelseId, is(melding.id));
    }

}
