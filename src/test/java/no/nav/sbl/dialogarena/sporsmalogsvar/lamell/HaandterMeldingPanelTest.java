package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.HaandterMeldingPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.AnimertJournalforingsPanel;
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
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.*;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.createMelding;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.createMeldingMedJournalfortDato;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class HaandterMeldingPanelTest extends WicketPageTest {

    private static final String HAANDTERMELDINGER_ID = "haandtermeldinger";
    private static final String NYOPPGAVE_VALG_ID = "nyoppgaveValg";
    private static final String JOURNALFOR_VALG_ID = "journalforingValg";
    private static final String MERKE_VALG_ID = "merkeValg";
    private static final String PRINT_ID = "print";

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    @Test
    public void skalKunneJournalforeHvisNyesteMeldingIkkeErJournalfort() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1"),
                createMelding("melding2", SVAR_SKRIFTLIG, now(), "TEMA", "melding1")));

        wicket.goToPageWith(new HaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsEnabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneJournalforeHvisNyesteMeldingErJournalfort() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMeldingMedJournalfortDato("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1", now())));

        wicket.goToPageWith(new HaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneJournalforeHvisTraadErMerketMedFeilsendt() {
        Melding melding = createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1");
        melding.markertSomFeilsendtAv = "feilsendtNavident";
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                melding));

        wicket.goToPageWith(new HaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneJournalforeHvisTraadErMerketMedKontorsperret() {
        Melding melding = createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1");
        melding.kontorsperretEnhet = "kontorsperretEnhet";
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                melding));

        wicket.goToPageWith(new HaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalKunneOppretteNyOppgaveHvisTraadenErBehandlet() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SAMTALEREFERAT_OPPMOTE, now().minusDays(1), "TEMA", "melding1")));

        wicket.goToPageWith(new HaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsEnabled().and(withId(NYOPPGAVE_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneOppretteNyOppgaveHvisTraadenIkkeErBehandlet() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1")));

        wicket.goToPageWith(new HaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsDisabled().and(withId(NYOPPGAVE_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneMerkeMeldingHvisEldsteMeldingErJournalfort() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMeldingMedJournalfortDato("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1", now())));

        wicket.goToPageWith(new HaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsDisabled().and(withId(MERKE_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneMerkeMeldingHvisTraadIkkeErBehandlet() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1")));

        wicket.goToPageWith(new HaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsDisabled().and(withId(MERKE_VALG_ID)));
    }

    @Test
    public void skalViseJournalforingsPanelogSkjuleNyOppgavePanelVedKlikkPaaJournalfor() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1"),
                createMelding("melding2", SVAR_SKRIFTLIG, now(), "TEMA", "melding1")));

        wicket.goToPageWith(new HaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .click().link(containedInComponent(withId(JOURNALFOR_VALG_ID)))
                .should().containComponent(thatIsVisible().and(ofType(AnimertJournalforingsPanel.class)))
                .should().containComponent(thatIsInvisible().and(ofType(OppgavePanel.class)));
    }

    @Test
    public void sideSkalInneholdeMulighetForPrint() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1")));

        wicket.goToPageWith(new HaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(withId(PRINT_ID));
    }

    @Test
    public void kanIkkeKunneJournalforeHvisSporsmalIkkeErBehandlet() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1")));

        wicket.goToPageWith(new HaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));
    }
}