package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.oppgave;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.MockServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static no.nav.modig.wicket.test.matcher.CombinableMatcher.both;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.createMelding;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {MockServiceTestContext.class})
@ExtendWith(SpringExtension.class)
public class OppgavePanelTest extends WicketPageTest {

    private Melding melding;
    private InnboksVM innboksVM = mock(InnboksVM.class);

    @BeforeEach
    public void setUp() {
        TraadVM traadVM = mock(TraadVM.class);
        String eldsteMeldingId = "id";
        melding = createMelding(eldsteMeldingId, SPORSMAL_SKRIFTLIG, now(), Temagruppe.ARBD, eldsteMeldingId);
        MeldingVM meldingVM = new MeldingVM(melding, 1);
        when(innboksVM.getValgtTraad()).thenReturn(traadVM);
        when(innboksVM.getSessionHenvendelseId()).thenReturn(empty());
        when(traadVM.getEldsteMelding()).thenReturn(meldingVM);
        when(innboksVM.getSessionOppgaveId()).thenReturn(of("1"));
    }

    @Test
    public void skalKjoereOppNyOppgavePanelMedRiktigeFelterOgTyper() {
        wicket.goToPageWith(new OppgavePanel("panel", innboksVM))
                .should().containComponent(both(ofType(Form.class).and(withId("nyoppgaveform"))))
                .should().containComponent(both(ofType(FeedbackPanel.class).and(withId("feedback"))))
                .should().containComponent(both(ofType(AjaxButton.class).and(withId("opprettoppgave"))))
                .should().containComponent(both(ofType(AjaxLink.class).and(withId("avbryt"))));
    }

    @Test
    public void skalLukkePaneletIdetManAvbryter() {
        OppgavePanel nyOppgavePanel = new OppgavePanel("panel", innboksVM);
        nyOppgavePanel.setVisibilityAllowed(true);
        wicket.goToPageWith(nyOppgavePanel)
                .should().containComponent(thatIsVisible().and(withId("panel")))
                .click().link(withId("avbryt"));

        assertThat(nyOppgavePanel.isVisibilityAllowed(), is(false));
    }

    @Test
    public void viserAvsluttValgHvisValgtTraadErISession() {
        when(innboksVM.getSessionHenvendelseId()).thenReturn(of("id"));

        TraadVM traadVM = mock(TraadVM.class);
        MeldingVM meldingVM = new MeldingVM(melding, 1);
        when(traadVM.getMeldinger()).thenReturn(asList(meldingVM));
        when(traadVM.getEldsteMelding()).thenReturn(meldingVM);

        when(innboksVM.getValgtTraad()).thenReturn(traadVM);

        wicket.goToPageWith(new OppgavePanel("panel", innboksVM).setVisibilityAllowed(true))
                .should().containComponent(thatIsVisible().and(withId("oppgaveValg")))
                .should().containComponent(thatIsVisible().and(withId("nyoppgaveForm")))
                .should().containComponent(thatIsInvisible().and(withId("avsluttOppgaveForm")));
    }

}
