package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.JournalforingsPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgave.NyOppgavePanel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.containedInComponent;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsDisabled;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsEnabled;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsInvisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SAMTALEREFERAT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SPORSMAL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SVAR;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMelding;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMeldingMedJournalfortDato;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class HaandterMeldingPanelTest extends WicketPageTest {

    private static final String HAANDTERMELDINGER_ID = "haandtermeldinger";
    private static final String BESVAR_ID = "besvar";
    private static final String NYOPPGAVE_VALG_ID = "nyoppgaveValg";
    private static final String JOURNALFOR_VALG_ID = "journalforingValg";
    private static final String MERKE_VALG_ID = "merkeValg";

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    @Test
    public void skalKunneBesvareTraadInitiertAvBruker() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL, now().minusDays(1), "TEMA", "traad1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM(henvendelseBehandlingService, "fnr")))
                .should().containComponent(thatIsEnabled().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalKunneBesvareTraadInitiertAvBrukerMedTidligereSvar() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL, now().minusDays(1), "TEMA", "traad1"),
                createMelding("melding2", SVAR, now(), "TEMA", "traad1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM(henvendelseBehandlingService, "fnr")))
                .should().containComponent(thatIsEnabled().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalIkkeKunneBesvareTraadInitiertAvSaksbehandler() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SAMTALEREFERAT, now().minusDays(1), "TEMA", "traad1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM(henvendelseBehandlingService, "fnr")))
                .should().containComponent(thatIsDisabled().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalKunneJournalforeHvisNyesteMeldingIkkeErJournalfort() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL, now().minusDays(1), "TEMA", "traad1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM(henvendelseBehandlingService, "fnr")))
                .should().containComponent(thatIsEnabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneJournalforeHvisNyesteMeldingErJournalfort() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMeldingMedJournalfortDato("melding1", SPORSMAL, now().minusDays(1), "TEMA", "traad1", now())));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM(henvendelseBehandlingService, "fnr")))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalKunneOppretteNyOppgaveHvisTraadenErBehandlet() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SAMTALEREFERAT, now().minusDays(1), "TEMA", "traad1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM(henvendelseBehandlingService, "fnr")))
                .should().containComponent(thatIsEnabled().and(withId(NYOPPGAVE_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneOppretteNyOppgaveHvisTraadenIkkeErBehandlet() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL, now().minusDays(1), "TEMA", "traad1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM(henvendelseBehandlingService, "fnr")))
                .should().containComponent(thatIsDisabled().and(withId(NYOPPGAVE_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneMerkeMeldingHvisEldsteMeldingErJournalfort() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMeldingMedJournalfortDato("melding1", SPORSMAL, now().minusDays(1), "TEMA", "traad1", now())));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM(henvendelseBehandlingService, "fnr")))
                .should().containComponent(thatIsDisabled().and(withId(MERKE_VALG_ID)));
    }


    @Test
    public void skalViseJournalforingsPanelogSkjuleNyOppgavePanelVedKlikkPaaJournalfor() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL, now().minusDays(1), "TEMA", "traad1")));

        wicket.goToPageWith(new TestHaandterMeldingPanel(HAANDTERMELDINGER_ID, new InnboksVM(henvendelseBehandlingService, "fnr")))
                .printComponentsTree()
                .click().link(containedInComponent(withId(JOURNALFOR_VALG_ID)))
                .should().containComponent(thatIsVisible().and(ofType(JournalforingsPanel.class)))
                .should().containComponent(thatIsInvisible().and(ofType(NyOppgavePanel.class)));
    }

}