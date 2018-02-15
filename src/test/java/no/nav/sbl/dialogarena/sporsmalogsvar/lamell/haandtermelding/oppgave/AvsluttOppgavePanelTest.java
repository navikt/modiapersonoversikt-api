package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.oppgave;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.MockServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Optional;

import static java.util.Optional.of;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MockServiceTestContext.class)
@DirtiesContext(classMode = AFTER_CLASS)
public class AvsluttOppgavePanelTest extends WicketPageTest {

    @Inject
    private GsakService gsakService;

    @Test
    public void avslutterOppgave() throws LagreOppgaveOptimistiskLasing, GsakService.OppgaveErFerdigstilt {
        Optional<String> oppgaveId = of("1");
        String tekst = "tekst";

        when(gsakService.hentOppgave(oppgaveId.get())).thenReturn(new WSOppgave());

        wicket.goToPageWith(new AvsluttOppgavePanel("id", oppgaveId.orElse("")))
                .inForm(withId("form"))
                .write("beskrivelse", tekst)
                .submitWithAjaxButton(withId("avsluttoppgave"))
                .should().containComponent(thatIsVisible().and(withId("feedbackAvsluttOppgave")));

        verify(gsakService, times(1)).ferdigstillGsakOppgave(any(WSOppgave.class), eq(tekst));
    }

    @Test
    public void viserFeilmeldingHvisFerdigstillingFeiler() throws LagreOppgaveOptimistiskLasing, GsakService.OppgaveErFerdigstilt {
        doThrow(new RuntimeException()).when(gsakService).ferdigstillGsakOppgave(any(WSOppgave.class), anyString());

        wicket.goToPageWith(new AvsluttOppgavePanel("id", "1"))
                .inForm(withId("form"))
                .submitWithAjaxButton(withId("avsluttoppgave"))
                .should().containComponent(thatIsVisible().and(withId("feedbackError")));
    }

}