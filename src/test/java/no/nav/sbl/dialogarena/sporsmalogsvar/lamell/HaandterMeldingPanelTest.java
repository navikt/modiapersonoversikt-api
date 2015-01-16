package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.JournalforingsPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.oppgave.OppgavePanel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Meldingstype.SAMTALEREFERAT_OPPMOTE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Meldingstype.SVAR_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMelding;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMeldingMedJournalfortDato;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class HaandterMeldingPanelTest extends WicketPageTest {

    private static final String HAANDTERMELDINGER_ID = "haandtermeldinger";
    private static final String BESVAR_ID = "besvar";
    private static final String NYOPPGAVE_VALG_ID = "nyoppgaveValg";
    private static final String JOURNALFOR_VALG_ID = "journalforingValg";
    private static final String MERKE_VALG_ID = "merkeValg";
    private static final String PRINT_ID = "print";

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    @Test
    public void skalKunneBesvareTraadInitiertAvBruker() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsEnabled().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalIkkeKunneBesvareTraadInitiertAvSaksbehandler() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SAMTALEREFERAT_OPPMOTE, now().minusDays(1), "TEMA", "melding1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsDisabled().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalKunneBesvareTraadInitiertAvBrukerMedTidligereSvar() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1"),
                createMelding("melding2", SVAR_SKRIFTLIG, now(), "TEMA", "melding1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsEnabled().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalKunneBesvareTraadSomErMarkertSomKontorsperret() {
        Melding melding = createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1");
        melding.kontorsperretEnhet = "kontorsperretEnhet";
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                melding));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsEnabled().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalKunneJournalforeHvisNyesteMeldingIkkeErJournalfort() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1"),
                createMelding("melding2", SVAR_SKRIFTLIG, now(), "TEMA", "melding1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsEnabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneJournalforeHvisNyesteMeldingErJournalfort() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMeldingMedJournalfortDato("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1", now())));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneJournalforeHvisTraadErMerketMedFeilsendt() {
        Melding melding = createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1");
        melding.markertSomFeilsendtAv = "feilsendtNavident";
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                melding));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneJournalforeHvisTraadErMerketMedKontorsperret() {
        Melding melding = createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1");
        melding.kontorsperretEnhet = "kontorsperretEnhet";
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                melding));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalKunneOppretteNyOppgaveHvisTraadenErBehandlet() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SAMTALEREFERAT_OPPMOTE, now().minusDays(1), "TEMA", "melding1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsEnabled().and(withId(NYOPPGAVE_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneOppretteNyOppgaveHvisTraadenIkkeErBehandlet() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsDisabled().and(withId(NYOPPGAVE_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneMerkeMeldingHvisEldsteMeldingErJournalfort() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMeldingMedJournalfortDato("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1", now())));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsDisabled().and(withId(MERKE_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneMerkeMeldingHvisTraadIkkeErBehandlet() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsDisabled().and(withId(MERKE_VALG_ID)));
    }

    @Test
    public void skalViseJournalforingsPanelogSkjuleNyOppgavePanelVedKlikkPaaJournalfor() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1"),
                createMelding("melding2", SVAR_SKRIFTLIG, now(), "TEMA", "melding1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .click().link(containedInComponent(withId(JOURNALFOR_VALG_ID)))
                .should().containComponent(thatIsVisible().and(ofType(JournalforingsPanel.class)))
                .should().containComponent(thatIsInvisible().and(ofType(OppgavePanel.class)));
    }

    @Test
    public void sideSkalInneholdeMulighetForPrint() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(withId(PRINT_ID));
    }

    @Test
    public void kanIkkeKunneJournalforeHvisSporsmalIkkeErBehandlet() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));
    }
}