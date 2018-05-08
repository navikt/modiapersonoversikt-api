package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.MockServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.domain.Meldinger;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.HaandterMeldingValgPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.MeldingActionPanel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.createMelding;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;

@DirtiesContext(classMode = BEFORE_CLASS)
@ContextConfiguration(classes = {MockServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class HaandterMeldingValgPanelTest extends WicketPageTest {

    private static final String HAANDTERMELDINGER_ID = "haandtermeldinger";
    private static final String NYOPPGAVE_VALG_ID = "nyoppgaveValg";
    private static final String JOURNALFOR_VALG_ID = "journalforingValg";
    private static final String MERKE_VALG_ID = "merkeValg";
    private static final String PRINT_ID = "print";

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @Inject
    private EnforcementPoint pep;

    @Test
    public void skalKunneJournalforeHvisNyesteMeldingIkkeErJournalfort() {
        when(henvendelseBehandlingService.hentMeldinger(anyString(), anyString())).thenReturn(new Meldinger(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), Temagruppe.ARBD, "melding1"),
                createMelding("melding2", SVAR_SKRIFTLIG, now(), Temagruppe.ARBD, "melding1"))));

        InnboksVM innboksVM = innboksVM();
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(thatIsEnabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneJournalforeHvisNyesteMeldingErJournalfort() {
        InnboksVM innboksVM = innboksVM();
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneJournalforeHvisTraadErMerketMedFeilsendt() {
        Melding melding = createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), Temagruppe.ARBD, "melding1");
        melding.markertSomFeilsendtAvNavIdent = "feilsendtNavident";

        InnboksVM innboksVM = innboksVM();
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneJournalforeHvisTraadErMerketMedKontorsperret() {
        Melding melding = createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), Temagruppe.ARBD, "melding1");
        melding.kontorsperretEnhet = "kontorsperretEnhet";

        InnboksVM innboksVM = innboksVM();
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneJournalforeHvisTraadHarTemagruppeForSosialeTjenester() {
        InnboksVM innboksVM = innboksVM();
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));

        InnboksVM innboksVM2 = innboksVM();
        MeldingActionPanel meldingActionPanel2 = new MeldingActionPanel("actionpanel", innboksVM2);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM2, meldingActionPanel2))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    @Test
    public void skalKunneOppretteNyOppgaveHvisTraadenErBehandlet() {
        when(henvendelseBehandlingService.hentMeldinger(anyString(), anyString())).thenReturn(new Meldinger(asList(
                createMelding("melding1", SAMTALEREFERAT_OPPMOTE, now().minusDays(1), Temagruppe.ARBD, "melding1"))));

        InnboksVM innboksVM = innboksVM();
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(thatIsEnabled().and(withId(NYOPPGAVE_VALG_ID)));
    }

    @Test
    public void skalIkkeKunneOppretteNyOppgaveHvisTraadenIkkeErBehandlet() {
        when(henvendelseBehandlingService.hentMeldinger(anyString(), anyString())).thenReturn(new Meldinger(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), Temagruppe.ARBD, "melding1"))));

        InnboksVM innboksVM = innboksVM();
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(thatIsDisabled().and(withId(NYOPPGAVE_VALG_ID)));
    }

    @Test
    public void skalKunneMerkeMeldingHvisMeldingErSporsmal() {
        when(henvendelseBehandlingService.hentMeldinger(anyString(), anyString())).thenReturn(new Meldinger(asList(
                createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), Temagruppe.ARBD, "melding1"))));

        InnboksVM innboksVM = innboksVM();
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(thatIsEnabled().and(withId(MERKE_VALG_ID)));
    }

    @Test
    public void skalKunneMerkeMeldingHvisMeldingErSporsmalOgMeldingErBehandlet() {
        InnboksVM innboksVM = innboksVM();
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM(), meldingActionPanel))
                .should().containComponent(thatIsEnabled().and(withId(MERKE_VALG_ID)));
    }

    @Test
    public void sideSkalInneholdeMulighetForPrint() {
        InnboksVM innboksVM = innboksVM();
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(withId(PRINT_ID));
    }

    @Test
    public void kanIkkeKunneJournalforeHvisSporsmalIkkeErBehandlet() {
        InnboksVM innboksVM = innboksVM();
        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("actionpanel", innboksVM);
        wicket.goToPageWith(new HaandterMeldingValgPanel(HAANDTERMELDINGER_ID, innboksVM, meldingActionPanel))
                .should().containComponent(thatIsDisabled().and(withId(JOURNALFOR_VALG_ID)));
    }

    private InnboksVM innboksVM() {
        InnboksVM innboksVM = new InnboksVM("fnr", henvendelseBehandlingService, pep, saksbehandlerInnstillingerService);
        innboksVM.oppdaterMeldinger();
        innboksVM.settForsteSomValgtHvisIkkeSatt();
        return innboksVM;
    }
}