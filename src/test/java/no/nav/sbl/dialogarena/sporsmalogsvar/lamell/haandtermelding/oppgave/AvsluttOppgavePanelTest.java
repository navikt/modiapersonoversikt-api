package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.oppgave;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ServiceTestContext.class)
public class AvsluttOppgavePanelTest extends WicketPageTest {

    @Inject
    private GsakService gsakService;

    @Test
    public void avslutterOppgave() {
        Optional<String> oppgaveId = optional("1");
        String tekst = "tekst";
        wicket.goToPageWith(new TestAvsluttOppgavePanel("id", oppgaveId))
                .inForm(withId("form"))
                .write("beskrivelse", tekst)
                .submitWithAjaxButton(withId("avsluttoppgave"))
                .should().containComponent(thatIsVisible().and(withId("feedbackAvsluttOppgave")));

        verify(gsakService, times(1)).ferdigstillGsakOppgave(oppgaveId, tekst);
    }


}