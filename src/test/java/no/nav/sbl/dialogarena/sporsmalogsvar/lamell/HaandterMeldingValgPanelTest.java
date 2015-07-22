package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.HaandterMeldingValgPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.MeldingActionPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.oppgave.OppgavePanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.*;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class HaandterMeldingValgPanelTest extends WicketPageTest {

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
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), Temagruppe.ARBD, "melding1"),
                createMelding("melding2", SVAR_SKRIFTLIG, now(), Temagruppe.ARBD, "melding1")));

        InnboksVM innboksVM = new InnboksVM("fnr", henvendelseBehandlingService);
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(thatIsEnabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneJournalforeHvisNyesteMeldingErJournalfort() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMeldingMedJournalfortDato("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1", now())));

        InnboksVM innboksVM = new InnboksVM("fnr", henvendelseBehandlingService);
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneJournalforeHvisTraadErMerketMedFeilsendt() {
        Melding melding = createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), Temagruppe.ARBD, "melding1");
        melding.markertSomFeilsendtAv = "feilsendtNavident";
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                melding));

        InnboksVM innboksVM = new InnboksVM("fnr", henvendelseBehandlingService);
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneJournalforeHvisTraadErMerketMedKontorsperret() {
        Melding melding = createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), Temagruppe.ARBD, "melding1");
        melding.kontorsperretEnhet = "kontorsperretEnhet";
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                melding));

        InnboksVM innboksVM = new InnboksVM("fnr", henvendelseBehandlingService);
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneJournalforeHvisTraadHarTemagruppeForSosialeTjenester() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), Temagruppe.OKSOS, "melding1"),
                createMelding("melding2", SVAR_SKRIFTLIG, now(), Temagruppe.OKSOS, "melding1")));

        InnboksVM innboksVM = new InnboksVM("fnr", henvendelseBehandlingService);
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));

        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), Temagruppe.ANSOS, "melding1"),
                createMelding("melding2", SVAR_SKRIFTLIG, now(), Temagruppe.ANSOS, "melding1")));

        InnboksVM innboksVM2 = new InnboksVM("fnr", henvendelseBehandlingService);
        MeldingActionPanel meldingActionPanel2 = new MeldingActionPanel("actionpanel", innboksVM2);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM2, meldingActionPanel2))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalKunneOppretteNyOppgaveHvisTraadenErBehandlet() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SAMTALEREFERAT_OPPMOTE, now().minusDays(1), Temagruppe.ARBD, "melding1")));

        InnboksVM innboksVM = new InnboksVM("fnr", henvendelseBehandlingService);
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(thatIsEnabled().and(withId(NYOPPGAVE_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneOppretteNyOppgaveHvisTraadenIkkeErBehandlet() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), Temagruppe.ARBD, "melding1")));

        InnboksVM innboksVM = new InnboksVM("fnr", henvendelseBehandlingService);
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(thatIsDisabled().and(withId(NYOPPGAVE_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneMerkeMeldingHvisEldsteMeldingErJournalfort() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMeldingMedJournalfortDato("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1", now())));

        InnboksVM innboksVM = new InnboksVM("fnr", henvendelseBehandlingService);
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(thatIsDisabled().and(withId(MERKE_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneMerkeMeldingHvisTraadIkkeErBehandlet() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), Temagruppe.ARBD, "melding1")));

        InnboksVM innboksVM = new InnboksVM("fnr", henvendelseBehandlingService);
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(thatIsDisabled().and(withId(MERKE_VALG_ID)));
    }

    @Test
    public void sideSkalInneholdeMulighetForPrint() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), Temagruppe.ARBD, "melding1")));

        InnboksVM innboksVM = new InnboksVM("fnr", henvendelseBehandlingService);
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(withId(PRINT_ID));
    }

    @Test
    public void kanIkkeKunneJournalforeHvisSporsmalIkkeErBehandlet() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), Temagruppe.ARBD, "melding1")));

        InnboksVM innboksVM = new InnboksVM("fnr", henvendelseBehandlingService);
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneMerkeFeilsendtPost() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMeldingSomErMarkertSomFeilsendt("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", "melding1", "x12345678")));

        InnboksVM innboksVM = new InnboksVM("fnr", henvendelseBehandlingService);
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(thatIsDisabled().and(withId(MERKE_VALG_ID)));
    }
}