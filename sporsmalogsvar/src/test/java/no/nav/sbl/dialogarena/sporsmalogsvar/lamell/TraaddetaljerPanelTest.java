package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.brukerdialog.security.context.ThreadLocalSubjectHandler;
import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.MockServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.domain.Meldinger;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.createMelding;
import static org.joda.time.DateTime.now;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;

@ContextConfiguration(classes = {MockServiceTestContext.class})
@DirtiesContext(classMode = BEFORE_CLASS)
@ExtendWith(SpringExtension.class)
public class TraaddetaljerPanelTest extends WicketPageTest {

    private static final String BESVAR_ID = "besvar";

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @Inject
    private EnforcementPoint pep;

    @BeforeAll
    public static void setUpAll() {
        System.setProperty("no.nav.brukerdialog.security.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
    }

    @Test
    public void skalKunneBesvareTraadInitiertAvBruker() {
        wicket.goToPageWith(new TraaddetaljerPanel("id", innboksVM()))
                .should().containComponent(thatIsVisible().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalIkkeKunneBesvareTraadInitiertAvSaksbehandler() {
        when(henvendelseBehandlingService.hentMeldinger(anyString(), anyString())).thenReturn(new Meldinger(asList(
                createMelding("melding1", SAMTALEREFERAT_OPPMOTE, now().minusDays(1), Temagruppe.ARBD, "melding1"))));

        wicket.goToPageWith(new TraaddetaljerPanel("id", innboksVM()))
                .should().containComponent(thatIsInvisible().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalKunneBesvareTraadInitiertAvBrukerMedTidligereSvar() {
        wicket.goToPageWith(new TraaddetaljerPanel("id", innboksVM()))
                .should().containComponent(thatIsVisible().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalIkkeKunneBesvareTraadSomErMarkertSomKontorsperret() {
        Melding melding = createStandardMelding();
        melding.meldingstype = SPORSMAL_MODIA_UTGAAENDE;
        melding.kontorsperretEnhet = "kontorsperretEnhet";
        when(henvendelseBehandlingService.hentMeldinger(anyString(), anyString())).thenReturn(new Meldinger(asList(melding)));

        wicket.goToPageWith(new TraaddetaljerPanel("id", innboksVM()))
                .should().containComponent(thatIsInvisible().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalKunneBesvareTraadSomErKontorsperretDersomDetErEnkeltstaaendeSporsmalFraBruker() {
        Melding melding = createStandardMelding();
        melding.meldingstype = SPORSMAL_SKRIFTLIG;
        melding.kontorsperretEnhet = "kontorsperretEnhet";

        wicket.goToPageWith(new TraaddetaljerPanel("id", innboksVM("fnr")))
                .should().containComponent(thatIsVisible().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalIkkeViseTemanavnDersomTraadensEldsteMeldingIkkeErJournalfort() {
        Melding melding = createStandardMelding();
        melding.journalfortTemanavn = "journalfortTemanavnSomIkkeSkalVises";

        wicket.goToPageWith(new TraaddetaljerPanel("id", innboksVM()))
                .should().notContainPatterns(melding.journalfortTemanavn);
    }

    @Test
    public void skalViseTemanavnDersomTraadensEldsteMeldingErJournalfort() {
        Melding melding = createStandardMelding();
        melding.journalfortDato = DateTime.now();
        melding.journalfortTemanavn = "journalfortTemanavnSomSkalVises";
        when(henvendelseBehandlingService.hentMeldinger(anyString(), anyString())).thenReturn(new Meldinger(asList(melding)));

        wicket.goToPageWith(new TraaddetaljerPanel("id", innboksVM()))
                .should().containPatterns(melding.journalfortTemanavn);
    }

    @Test
    public void skalIkkeKunneBesvareTraadSomErMarkertSomFeilsendt() {
        Melding melding = createStandardMelding();
        String fnr = "11111111111";
        melding.markertSomFeilsendtAv = new Saksbehandler("", "", "navIdent");
        when(henvendelseBehandlingService.hentMeldinger(eq(fnr), anyString())).thenReturn(new Meldinger(asList(melding)));

        wicket.goToPageWith(new TraaddetaljerPanel("id", innboksVM(fnr)))
                .should().containComponent(thatIsInvisible().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalIkkeViseSaksbehandlersNavnHvisMeldingFraBruker() {
        String fnr = "11111111111";
        Melding melding = createStandardMelding();
        melding.meldingstype = SPORSMAL_SKRIFTLIG;

        wicket.goToPageWith(new TraaddetaljerPanel("id", innboksVM(fnr)))
                .should().containComponent(thatIsInvisible().and(withId("skrevetAvContainer")));
    }

    @Test
    public void skalViseSaksbehandlersNavnHvisMeldingFraNAV() {
        String fnr = "11111111111";
        Melding melding = createStandardMelding();
        melding.meldingstype = SAMTALEREFERAT_OPPMOTE;
        melding.navIdent = "ident";
        when(henvendelseBehandlingService.hentMeldinger(eq(fnr), anyString())).thenReturn(new Meldinger(asList(melding)));

        wicket.goToPageWith(new TraaddetaljerPanel("id", innboksVM(fnr)))
                .should().containComponent(thatIsVisible().and(withId("skrevetAvContainer")));
    }

    private InnboksVM innboksVM() {
        return innboksVM("209380283");
    }

    private InnboksVM innboksVM(String fnr) {
        InnboksVM innboksVM = new InnboksVM(fnr, henvendelseBehandlingService, pep, saksbehandlerInnstillingerService);
        innboksVM.oppdaterMeldinger();
        innboksVM.settForsteSomValgtHvisIkkeSatt();
        return innboksVM;
    }

    private Melding createStandardMelding() {
        return createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), Temagruppe.ARBD, "melding1");
    }
}