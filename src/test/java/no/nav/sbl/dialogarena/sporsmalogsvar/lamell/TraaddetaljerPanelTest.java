package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Person;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.createMelding;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class TraaddetaljerPanelTest extends WicketPageTest {

    private static final String BESVAR_ID = "besvar";

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;
    @Inject
    private LDAPService ldapService;

    @Before
    public void before() {
        when(ldapService.hentSaksbehandler(anyString())).thenReturn(new Person("", ""));
    }

    @Test
    public void skalKunneBesvareTraadInitiertAvBruker() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(createStandardMelding()));

        wicket.goToPageWith(new TraaddetaljerPanel("id", new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsVisible().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalIkkeKunneBesvareTraadInitiertAvSaksbehandler() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("melding1", SAMTALEREFERAT_OPPMOTE, now().minusDays(1), Temagruppe.ARBD, "melding1")));

        wicket.goToPageWith(new TraaddetaljerPanel("id", new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsInvisible().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalKunneBesvareTraadInitiertAvBrukerMedTidligereSvar() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createStandardMelding(),
                createMelding("melding2", SVAR_SKRIFTLIG, now(), Temagruppe.ARBD, "melding1")));

        wicket.goToPageWith(new TraaddetaljerPanel("id", new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsVisible().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalIkkeKunneBesvareTraadSomErMarkertSomKontorsperret() {
        Melding melding = createStandardMelding();
        melding.meldingstype = SPORSMAL_MODIA_UTGAAENDE;
        melding.kontorsperretEnhet = "kontorsperretEnhet";
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(melding));

        wicket.goToPageWith(new TraaddetaljerPanel("id", new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsInvisible().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalKunneBesvareTraadSomErKontorsperretDersomDetErEnkeltstaaendeSporsmalFraBruker() {
        Melding melding = createStandardMelding();
        melding.meldingstype = SPORSMAL_SKRIFTLIG;
        melding.kontorsperretEnhet = "kontorsperretEnhet";
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(melding));

        wicket.goToPageWith(new TraaddetaljerPanel("id", new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containComponent(thatIsVisible().and(withId(BESVAR_ID)));
    }

    @Test
    public void skalIkkeViseTemanavnDersomTraadensEldsteMeldingIkkeErJournalfort() {
        Melding melding = createStandardMelding();
        melding.journalfortTemanavn = "journalfortTemanavnSomIkkeSkalVises";
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(melding));

        wicket.goToPageWith(new TraaddetaljerPanel("id", new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().notContainPatterns(melding.journalfortTemanavn);
    }

    @Test
    public void skalViseTemanavnDersomTraadensEldsteMeldingErJournalfort() {
        Melding melding = createStandardMelding();
        melding.journalfortDato = DateTime.now();
        melding.journalfortTemanavn = "journalfortTemanavnSomSkalVises";
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(melding));

        wicket.goToPageWith(new TraaddetaljerPanel("id", new InnboksVM("fnr", henvendelseBehandlingService)))
                .should().containPatterns(melding.journalfortTemanavn);
    }

    private Melding createStandardMelding() {
        return createMelding("melding1", SPORSMAL_SKRIFTLIG, now().minusDays(1), Temagruppe.ARBD, "melding1");
    }

    @Test
    public void skalIkkeKunneBesvareTraadSomErMarkertSomFeilsendt() {
        Melding melding = createStandardMelding();
        String fnr = "13245679810";
        melding.markertSomFeilsendtAv = "navIdent";
        when(henvendelseBehandlingService.hentMeldinger(fnr)).thenReturn(asList(melding));

        wicket.goToPageWith(new TraaddetaljerPanel("id", new InnboksVM(fnr, henvendelseBehandlingService)))
                .should().containComponent(thatIsInvisible().and(withId(BESVAR_ID)));
    }
}