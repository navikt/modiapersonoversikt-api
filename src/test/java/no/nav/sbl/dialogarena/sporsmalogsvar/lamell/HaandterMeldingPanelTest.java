package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.JournalforingsPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.nyoppgave.NyOppgavePanel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsDisabled;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsEnabled;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsInvisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SAMTALEREFERAT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SPORSMAL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SVAR;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.createMelding;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.createMeldingMedJournalfortDato;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class HaandterMeldingPanelTest extends WicketPageTest {

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    @Test
    public void skalKunneBesvareTraadInitiertAvBruker() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL, now().minusDays(1), "TEMA", "traad1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel("haandtermeldinger", new InnboksVM(henvendelseBehandlingService, "fnr")))
                .should().containComponent(thatIsEnabled().and(withId("besvar")));
    }

    @Test
    public void skalKunneBesvareTraadInitiertAvBrukerMedTidligereSvar() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL, now().minusDays(1), "TEMA", "traad1"),
                createMelding("melding2", SVAR, now(), "TEMA", "traad1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel("haandtermeldinger", new InnboksVM(henvendelseBehandlingService, "fnr")))
                .should().containComponent(thatIsEnabled().and(withId("besvar")));
    }

    @Test
    public void skalIkkeKunneBesvareTraadInitiertAvSaksbehandler() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SAMTALEREFERAT, now().minusDays(1), "TEMA", "traad1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel("haandtermeldinger", new InnboksVM(henvendelseBehandlingService, "fnr")))
                .should().containComponent(thatIsDisabled().and(withId("besvar")));
    }

    @Test
    public void skalKunneJournalforeHvisNyesteMeldingIkkeErJournalfort() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL, now().minusDays(1), "TEMA", "traad1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel("haandtermeldinger", new InnboksVM(henvendelseBehandlingService, "fnr")))
                .should().containComponent(thatIsEnabled().and(withId("journalfor")));
    }

    @Test
    public void skalIkkeKunneJournalforeHvisNyesteMeldingErJournalfort() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMeldingMedJournalfortDato("melding1", SPORSMAL, now().minusDays(1), "TEMA", "traad1", now())));

        wicket.goToPageWith(new TestHaandterMeldingPanel("haandtermeldinger", new InnboksVM(henvendelseBehandlingService, "fnr")))
                .should().containComponent(thatIsDisabled().and(withId("journalfor")));
    }

    @Test
    public void skalKunneOppretteNyOppgaveHvisTraadenErBehandlet() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SAMTALEREFERAT, now().minusDays(1), "TEMA", "traad1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel("haandtermeldinger", new InnboksVM(henvendelseBehandlingService, "fnr")))
                .should().containComponent(thatIsEnabled().and(withId("nyoppgave")));
    }

    @Test
    public void skalIkkeKunneOppretteNyOppgaveHvisTraadenIkkeErBehandlet() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL, now().minusDays(1), "TEMA", "traad1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel("haandtermeldinger", new InnboksVM(henvendelseBehandlingService, "fnr")))
                .should().containComponent(thatIsDisabled().and(withId("nyoppgave")));
    }

    @Test
    public void skalViseJournalforingsPanelogSkjuleNyOppgavePanelVedKlikkPaaJournalfor() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL, now().minusDays(1), "TEMA", "traad1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel("haandtermeldinger", new InnboksVM(henvendelseBehandlingService, "fnr")))
                .click().link(withId("journalfor"))
                .should().containComponent(thatIsVisible().and(ofType(JournalforingsPanel.class)))
                .should().containComponent(thatIsInvisible().and(ofType(NyOppgavePanel.class)));
    }

    @Test
    public void skalViseNyOppgavePanelogSkjuleJournalforingsPanelVedKlikkPaaNyOppgave() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SAMTALEREFERAT, now().minusDays(1), "TEMA", "traad1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel("haandtermeldinger", new InnboksVM(henvendelseBehandlingService, "fnr")))
                .click().link(withId("nyoppgave"))
                .should().containComponent(thatIsVisible().and(ofType(NyOppgavePanel.class)))
                .should().containComponent(thatIsInvisible().and(ofType(JournalforingsPanel.class)));
    }

}