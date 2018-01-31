package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.oppgave;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNAVEnhetListe;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.*;
import no.nav.brukerdialog.security.context.SubjectHandler;
import no.nav.brukerdialog.security.context.SubjectHandlerUtils;
import no.nav.brukerdialog.security.context.ThreadLocalSubjectHandler;
import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.brukerdialog.tools.SecurityConstants;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.FeatureToggle;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.oppgavebehandling.OppgaveBehandlingServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.AnsattServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.arbeidsfordeling.ArbeidsfordelingV1ServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.util.HttpRequestUtil;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.util.SubjectHandlerUtil;
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.*;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveResponse;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSEndreOppgave;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSLagreOppgaveRequest;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.ws.rs.ForbiddenException;

import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature.DELVISE_SVAR;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

class OppgaveControllerTest {

    public static final String OPPGAVE_ID = "OPPGAVE_ID";
    public static final String BRUKERS_FODSELSNUMMER = "10108000398";
    public static final String SAKSBEHANDLERS_IDENT = "SAKSBEHANDLER";
    public static final String UNDERKATEGORI_KODE_FOR_TEMAGRUPPE_ARBEID = "ARBD_KNA";
    public static final String TEMAGRUPPE_ARBEID = "ARBD";
    public static final String VALGT_ENHET = "4300";

    private OppgaveController oppgaveController;
    private OppgavebehandlingV3 oppgaveBehandlingMock;
    private OppgaveV3 oppgaveWSMock;
    private AnsattServiceImpl ansattWSMock;

    @BeforeAll
    static void beforeAll() {
        FeatureToggle.toggleFeature(DELVISE_SVAR);
    }

    @AfterAll
    static void afterAll() {
        FeatureToggle.disableFeature(DELVISE_SVAR);
    }

    @BeforeEach
    void before() throws HentOppgaveOppgaveIkkeFunnet, HentNAVAnsattFaultGOSYSGeneriskfMsg, HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg, HentNAVAnsattEnhetListeFaultGOSYSGeneriskMsg,
            HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg {
        SubjectHandlerUtil.setInnloggetSaksbehandler(SAKSBEHANDLERS_IDENT);
        setupMocks();
        OppgaveBehandlingServiceImpl oppgaveBehandlingService = new OppgaveBehandlingServiceImpl(oppgaveBehandlingMock, oppgaveWSMock, ansattWSMock, mock(ArbeidsfordelingV1ServiceImpl.class));

        oppgaveController = new OppgaveController(oppgaveBehandlingService);
    }

    private void setupMocks() throws HentOppgaveOppgaveIkkeFunnet, HentNAVAnsattEnhetListeFaultGOSYSGeneriskMsg, HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg, HentNAVAnsattFaultGOSYSGeneriskfMsg, HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg {
        oppgaveBehandlingMock = mock(OppgavebehandlingV3.class);
        oppgaveWSMock = mockOppgaveWs();
        ansattWSMock = new AnsattServiceImpl(mockGosysNavAnsatt());
    }

    private OppgaveV3 mockOppgaveWs() throws HentOppgaveOppgaveIkkeFunnet {
        OppgaveV3 oppgaveWS = mock(OppgaveV3.class);
        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(new WSHentOppgaveResponse().withOppgave(mockOppgaveFraGSAK()));
        return oppgaveWS;
    }

    private GOSYSNAVansatt mockGosysNavAnsatt() throws HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg, HentNAVAnsattFaultGOSYSGeneriskfMsg, HentNAVAnsattEnhetListeFaultGOSYSGeneriskMsg,
            HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg {
        GOSYSNAVansatt gosysnaVansatt = mock(GOSYSNAVansatt.class);
        when(gosysnaVansatt.hentNAVAnsatt(any(ASBOGOSYSNAVAnsatt.class))).thenReturn(new ASBOGOSYSNAVAnsatt());
        ASBOGOSYSNAVEnhetListe navEnhetListe = new ASBOGOSYSNAVEnhetListe();
        navEnhetListe.getNAVEnheter().add(new ASBOGOSYSNavEnhet());
        when(gosysnaVansatt.hentNAVAnsattEnhetListe(any(ASBOGOSYSNAVAnsatt.class))).thenReturn(navEnhetListe);
        return gosysnaVansatt;

    }

    private WSOppgave mockOppgaveFraGSAK() {
        return new WSOppgave()
                .withAnsvarligId(SAKSBEHANDLERS_IDENT)
                .withOppgaveId(OPPGAVE_ID)
                .withOppgavetype(new WSOppgavetype())
                .withFagomrade(new WSFagomrade())
                .withPrioritet(new WSPrioritet())
                .withUnderkategori(new WSUnderkategori())
                .withVersjon(5)
                .withLest(false)
                .withGjelder(new WSBruker().withBrukerId(BRUKERS_FODSELSNUMMER));
    }

    @Test
    @DisplayName("Legger tilbake oppgave ved å kalle lagreOppgave mot GSAK")
    void leggerTilbakeOppgave() throws LagreOppgaveOptimistiskLasing, LagreOppgaveOppgaveIkkeFunnet {
        ArgumentCaptor<WSLagreOppgaveRequest> argumentCaptor = ArgumentCaptor.forClass(WSLagreOppgaveRequest.class);
        MockHttpServletRequest httpRequest = HttpRequestUtil.mockHttpServletRequestMedCookie(SAKSBEHANDLERS_IDENT, VALGT_ENHET);

        oppgaveController.leggTilbake(OPPGAVE_ID, httpRequest, lagRequest());

        verify(oppgaveBehandlingMock).lagreOppgave(argumentCaptor.capture());
        WSEndreOppgave oppgave = argumentCaptor.getValue().getEndreOppgave();

        assertAll("Oppgave lagret i GSAK",
                () -> assertEquals(OPPGAVE_ID, oppgave.getOppgaveId()),
                () -> assertEquals("", oppgave.getAnsvarligId()),
                () -> assertEquals(UNDERKATEGORI_KODE_FOR_TEMAGRUPPE_ARBEID, oppgave.getUnderkategoriKode()),
                () -> assertThat(oppgave.getBeskrivelse(), containsString(VALGT_ENHET))
        );
    }

    @Test
    @DisplayName("Legger tilbake oppgave med ugyldig temagruppe kaster feil")
    void sjekkUgyldigTemagruppe() throws Exception{
        LeggTilbakeRESTRequest leggTilbakeRESTRequest = lagRequest();
        leggTilbakeRESTRequest.temagruppe = "UGYLDG_TEMAGRUPPE";

        IllegalArgumentException assertion = assertThrows(IllegalArgumentException.class, ()-> oppgaveController.leggTilbake(OPPGAVE_ID, new MockHttpServletRequest(), leggTilbakeRESTRequest));

        assertTrue(assertion.getMessage().contains("Ugyldig temagruppe"));
    }

    @Test
    @DisplayName("Sjekker at ansvarlig for oppgaven er samme person som forsøker å legge den tilbake")
    void validererTilgang() throws LagreOppgaveOptimistiskLasing, LagreOppgaveOppgaveIkkeFunnet, HentOppgaveOppgaveIkkeFunnet {
        SubjectHandlerUtil.setInnloggetSaksbehandler("annen-saksbehandler");

        assertThrows(ForbiddenException.class, () -> {
            MockHttpServletRequest httpRequest = HttpRequestUtil.mockHttpServletRequestMedCookie("annen-saksbehandler", VALGT_ENHET);
            oppgaveController.leggTilbake(OPPGAVE_ID, httpRequest, lagRequest());
        });
    }

    private LeggTilbakeRESTRequest lagRequest() {
        LeggTilbakeRESTRequest leggTilbakeRESTRequest = new LeggTilbakeRESTRequest();
        leggTilbakeRESTRequest.temagruppe = TEMAGRUPPE_ARBEID;
        return leggTilbakeRESTRequest;
    }

}
