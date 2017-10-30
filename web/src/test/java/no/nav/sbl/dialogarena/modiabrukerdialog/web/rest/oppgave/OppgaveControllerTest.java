package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.oppgave;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNAVEnhetListe;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.*;
import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.FeatureToggle;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.AnsattServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingServiceImpl;
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.*;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveResponse;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.*;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSEndreOppgave;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSLagreOppgaveRequest;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

class OppgaveControllerTest {

    public static final String OPPGAVE_ID = "OPPGAVE_ID";
    public static final String BRUKERS_FODSELSNUMMER = "10108000398";
    public static final String SAKSBEHANDLERS_ID = "SAKSBEHANDLER";

    private OppgaveController oppgaveController;
    private OppgavebehandlingV3 oppgaveBehandlingMock;
    private OppgaveV3 oppgaveWS;

    @BeforeAll
    static void beforeAll() {
        FeatureToggle.enableDelviseSvarFunksjonalitet();
    }

    @AfterAll
    static void afterAll() {
        FeatureToggle.disableDelviseSvarFunksjonalitet();
    }

    @BeforeEach
    void before() throws HentOppgaveOppgaveIkkeFunnet, HentNAVAnsattFaultGOSYSGeneriskfMsg, HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg, HentNAVAnsattEnhetListeFaultGOSYSGeneriskMsg, HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg {
        setupSubjectHandler();
        oppgaveBehandlingMock = mock(OppgavebehandlingV3.class);
        oppgaveWS = mockOppgaveWs();

        AnsattServiceImpl ansattWS = new AnsattServiceImpl(mockGosysNavAnsatt());
        SaksbehandlerInnstillingerService saksbehandlerInnstillingerService = mock(SaksbehandlerInnstillingerService.class);

        OppgaveBehandlingServiceImpl oppgaveBehandlingService = new OppgaveBehandlingServiceImpl(oppgaveBehandlingMock, oppgaveWS, saksbehandlerInnstillingerService, ansattWS, null);
        oppgaveController = new OppgaveController(oppgaveBehandlingService);
    }

    private void setupSubjectHandler() {
        System.setProperty(SubjectHandler.SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getCanonicalName());
    }

    private GOSYSNAVansatt mockGosysNavAnsatt() throws HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg, HentNAVAnsattFaultGOSYSGeneriskfMsg, HentNAVAnsattEnhetListeFaultGOSYSGeneriskMsg, HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg {
        GOSYSNAVansatt gosysnaVansatt = mock(GOSYSNAVansatt.class);
        when(gosysnaVansatt.hentNAVAnsatt(any(ASBOGOSYSNAVAnsatt.class))).thenReturn(new ASBOGOSYSNAVAnsatt());
        ASBOGOSYSNAVEnhetListe navEnhetListe = new ASBOGOSYSNAVEnhetListe();
        navEnhetListe.getNAVEnheter().add(new ASBOGOSYSNavEnhet());
        when(gosysnaVansatt.hentNAVAnsattEnhetListe(any(ASBOGOSYSNAVAnsatt.class))).thenReturn(navEnhetListe);
        return gosysnaVansatt;
    }

    private OppgaveV3 mockOppgaveWs() throws HentOppgaveOppgaveIkkeFunnet {
        OppgaveV3 oppgaveWS = mock(OppgaveV3.class);
        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(new WSHentOppgaveResponse().withOppgave(mockOppgaveFraGSAK()));
        return oppgaveWS;
    }

    private WSOppgave mockOppgaveFraGSAK() {
        return new WSOppgave()
                .withAnsvarligId(SAKSBEHANDLERS_ID)
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

        oppgaveController.put(OPPGAVE_ID, new MockHttpServletRequest(), null);

        verify(oppgaveBehandlingMock).lagreOppgave(argumentCaptor.capture());
        WSEndreOppgave oppgave = argumentCaptor.getValue().getEndreOppgave();
        assertEquals(OPPGAVE_ID, oppgave.getOppgaveId());
    }

    @Test
    @DisplayName("Legger tilbake oppgave, setter ansvarlig for oppgaven til tom streng")
    void leggerTilbakeOppgaveSetterAnsvarligTilTom() throws LagreOppgaveOptimistiskLasing, LagreOppgaveOppgaveIkkeFunnet, HentOppgaveOppgaveIkkeFunnet {
        ArgumentCaptor<WSLagreOppgaveRequest> argumentCaptor = ArgumentCaptor.forClass(WSLagreOppgaveRequest.class);
        assumeTrue(oppgaveWS.hentOppgave(new WSHentOppgaveRequest()).getOppgave().getAnsvarligId().equals(SAKSBEHANDLERS_ID));

        oppgaveController.put(OPPGAVE_ID, new MockHttpServletRequest(), null);

        verify(oppgaveBehandlingMock).lagreOppgave(argumentCaptor.capture());
        WSEndreOppgave oppgave = argumentCaptor.getValue().getEndreOppgave();
        assertEquals("", oppgave.getAnsvarligId());
    }

}
