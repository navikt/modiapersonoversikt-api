package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSGeneriskfMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg;
import no.nav.sbl.dialogarena.abac.AbacRequest;
import no.nav.sbl.dialogarena.abac.AbacResponse;
import no.nav.sbl.dialogarena.abac.Decision;
import no.nav.sbl.dialogarena.abac.Response;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.SubjectHandlerUtil;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollContext;
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSBruker;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnOppgaveListeRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnOppgaveListeResponse;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveResponse;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSEndreOppgave;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSFerdigstillOppgaveBolkRequest;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSLagreOppgaveRequest;
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.TildelOppgaveV1;
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.WSTildelFlereOppgaverRequest;
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.WSTildelFlereOppgaverResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.OppgaveBehandlingServiceImpl.DEFAULT_ENHET;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.OppgaveMockFactory.lagWSOppgave;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.OppgaveMockFactory.mockHentOppgaveResponseMedTilordning;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Collections.asSet;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class OppgaveBehandlingServiceImplTest {

    public static final String SAKSBEHANDLERS_VALGTE_ENHET = "4100";
    @Captor
    ArgumentCaptor<WSFerdigstillOppgaveBolkRequest> ferdigstillOppgaveBolkRequestCaptor;
    @Captor
    ArgumentCaptor<WSLagreOppgaveRequest> lagreOppgaveRequestCaptor;
    @Captor
    ArgumentCaptor<WSTildelFlereOppgaverRequest> tildelFlereOppgaverRequestCaptor;
    @Captor
    ArgumentCaptor<WSHentOppgaveRequest> hentOppgaveRequestCaptor;

    @Mock
    private AnsattService ansattWS;
    @Mock
    private OppgaveV3 oppgaveWS;
    @Mock
    private OppgavebehandlingV3 oppgavebehandlingWS;
    @Mock
    private TildelOppgaveV1 tildelOppgaveWS;

    // Kan ikke bruke `@Mock` siden vi er avhengig av at verdien er definert ved opprettelsen av `Tilgangskontroll`
    private final TilgangskontrollContext tilgangskontrollContext = mock(TilgangskontrollContext.class);
    @Spy
    private final Tilgangskontroll tilgangskontroll = new Tilgangskontroll(tilgangskontrollContext);



    @InjectMocks
    private OppgaveBehandlingServiceImpl oppgaveBehandlingService;

    private static final String OPPGAVE_ID_1 = "123";
    private static final String OPPGAVE_ID_2 = "456";

    @BeforeEach
    public void init() {
        initMocks(this);
    }

    @Test
    public void skalHenteSporsmaalOgTilordneIGsak() throws HentOppgaveOppgaveIkkeFunnet, LagreOppgaveOppgaveIkkeFunnet, LagreOppgaveOptimistiskLasing{
        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(mockHentOppgaveResponse());

        SubjectHandlerUtil.withIdent("Z999999", () ->
            oppgaveBehandlingService.tilordneOppgaveIGsak("oppgaveid", Temagruppe.ARBD, SAKSBEHANDLERS_VALGTE_ENHET)
        );

        verify(oppgavebehandlingWS).lagreOppgave(lagreOppgaveRequestCaptor.capture());
        WSLagreOppgaveRequest request = lagreOppgaveRequestCaptor.getValue();

        assertThat(request.getEndreOppgave().getAnsvarligId(), is("Z999999"));
        assertThat(request.getEndretAvEnhetId(), is(DEFAULT_ENHET));
    }

    @Test
    public void skalPlukkeOppgaverFraGsak() throws HentOppgaveOppgaveIkkeFunnet {
        WSTildelFlereOppgaverResponse tildelFlereOppgaverResponse = new WSTildelFlereOppgaverResponse()
                .withOppgaveIder(Integer.valueOf(OPPGAVE_ID_1), Integer.valueOf(OPPGAVE_ID_2));

        WSHentOppgaveResponse hentOppgaveResponse1 = new WSHentOppgaveResponse()
                .withOppgave(lagWSOppgave().withOppgaveId(OPPGAVE_ID_1));
        WSHentOppgaveResponse hentOppgaveResponse2 = new WSHentOppgaveResponse()
                .withOppgave(lagWSOppgave().withOppgaveId(OPPGAVE_ID_2));

        when(tildelOppgaveWS.tildelFlereOppgaver(any(WSTildelFlereOppgaverRequest.class)))
                .thenReturn(tildelFlereOppgaverResponse);
        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class)))
                .thenReturn(hentOppgaveResponse1, hentOppgaveResponse2);

        SubjectHandlerUtil.withIdent("Z999999", () ->
            oppgaveBehandlingService.plukkOppgaverFraGsak(Temagruppe.ARBD, SAKSBEHANDLERS_VALGTE_ENHET)
        );

        verify(tildelOppgaveWS).tildelFlereOppgaver(tildelFlereOppgaverRequestCaptor.capture());
        verify(oppgaveWS, times(2)).hentOppgave(hentOppgaveRequestCaptor.capture());
        assertThat(hentOppgaveRequestCaptor.getAllValues().stream()
                        .map(WSHentOppgaveRequest::getOppgaveId)
                        .collect(toSet()),
                is(asSet(OPPGAVE_ID_1, OPPGAVE_ID_2)));
        assertNotNull(tildelFlereOppgaverRequestCaptor.getValue());
        assertThat(tildelFlereOppgaverRequestCaptor.getValue().getFagomrade(), is("KNA"));
        assertThat(tildelFlereOppgaverRequestCaptor.getValue().getOppgavetype(), is("SPM_OG_SVR"));
    }

    @Test
    public void skalFerdigstilleOppgaveFraGsak() throws HentOppgaveOppgaveIkkeFunnet, HentNAVAnsattFaultGOSYSGeneriskfMsg, HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg {
        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(mockHentOppgaveResponse());
        when(ansattWS.hentAnsattNavn(anyString())).thenReturn("");

        SubjectHandlerUtil.withIdent("Z999999", () ->
            oppgaveBehandlingService.ferdigstillOppgaveIGsak("1", Temagruppe.ARBD, SAKSBEHANDLERS_VALGTE_ENHET)
        );
        verify(oppgavebehandlingWS).ferdigstillOppgaveBolk(ferdigstillOppgaveBolkRequestCaptor.capture());
        assertThat(ferdigstillOppgaveBolkRequestCaptor.getValue().getOppgaveIdListe().get(0), is("1"));
    }

    @Test
    public void systemetLeggerTilbakeOppgaveIGsakUtenEndringer() throws HentOppgaveOppgaveIkkeFunnet, LagreOppgaveOptimistiskLasing, LagreOppgaveOppgaveIkkeFunnet {
        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(mockHentOppgaveResponseMedTilordning());

        oppgaveBehandlingService.systemLeggTilbakeOppgaveIGsak("1", Temagruppe.ARBD, SAKSBEHANDLERS_VALGTE_ENHET);

        verify(oppgavebehandlingWS).lagreOppgave(lagreOppgaveRequestCaptor.capture());
        WSEndreOppgave endreOppgave = lagreOppgaveRequestCaptor.getValue().getEndreOppgave();
        assertThat(endreOppgave.getBeskrivelse(), is(mockHentOppgaveResponseMedTilordning().getOppgave().getBeskrivelse()));
    }

    private WSHentOppgaveResponse mockHentOppgaveResponse() {
        return new WSHentOppgaveResponse().withOppgave(lagWSOppgave());
    }

    @Test
    public void skalKonvertereFraWSOppgaveTilWSEndreOppgave() {
        WSOppgave oppgave = lagWSOppgave();

        WSEndreOppgave endreOppgave = OppgaveBehandlingServiceImpl.tilWSEndreOppgave(oppgave);

        assertThat(endreOppgave.getOppgaveId(), is(oppgave.getOppgaveId()));
        assertThat(endreOppgave.getAnsvarligId(), is(oppgave.getAnsvarligId()));
        assertThat(endreOppgave.getBrukerId(), is(oppgave.getGjelder().getBrukerId()));
        assertThat(endreOppgave.getDokumentId(), is(oppgave.getDokumentId()));
        assertThat(endreOppgave.getKravId(), is(oppgave.getKravId()));
        assertThat(endreOppgave.getAnsvarligEnhetId(), is(oppgave.getAnsvarligEnhetId()));

        assertThat(endreOppgave.getFagomradeKode(), is(oppgave.getFagomrade().getKode()));
        assertThat(endreOppgave.getOppgavetypeKode(), is(oppgave.getOppgavetype().getKode()));
        assertThat(endreOppgave.getPrioritetKode(), is(oppgave.getPrioritet().getKode()));
        assertThat(endreOppgave.getBrukertypeKode(), is(oppgave.getGjelder().getBrukertypeKode()));
        assertThat(endreOppgave.getUnderkategoriKode(), is(oppgave.getUnderkategori().getKode()));

        assertThat(endreOppgave.getAktivFra(), is(oppgave.getAktivFra()));
        assertThat(endreOppgave.getBeskrivelse(), is(oppgave.getBeskrivelse()));
        assertThat(endreOppgave.getVersjon(), is(oppgave.getVersjon()));
        assertThat(endreOppgave.getSaksnummer(), is(oppgave.getSaksnummer()));
        assertThat(endreOppgave.isLest(), is(oppgave.isLest()));

    }

    @Test
    void skalFinneTilordnaOppgaver() throws HentOppgaveOppgaveIkkeFunnet {
        List<WSOppgave> oppgaveliste = Arrays.asList(
                lagWSOppgave().withOppgaveId("1").withGjelder( new WSBruker().withBrukerId("10108000398")),
                lagWSOppgave().withOppgaveId("2").withGjelder( new WSBruker().withBrukerId("10108000398"))
        );
        when(oppgaveWS.finnOppgaveListe(any(WSFinnOppgaveListeRequest.class)))
                .thenReturn(new WSFinnOppgaveListeResponse()
                        .withOppgaveListe(oppgaveliste)
                        .withTotaltAntallTreff(oppgaveliste.size()));

        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(mockHentOppgaveResponseMedTilordning());

        List<Oppgave> resultat = SubjectHandlerUtil.withIdent("Z999999", () -> oppgaveBehandlingService.finnTildelteOppgaverIGsak());


        when(tilgangskontrollContext.checkAbac(any(AbacRequest.class))).thenReturn(
                new AbacResponse(singletonList(new Response(Decision.Permit,emptyList())))
        );
        assertThat(resultat.size(), is(oppgaveliste.size()));
        assertThat(resultat.get(0).oppgaveId, is(oppgaveliste.get(0).getOppgaveId()));
        assertThat(resultat.get(1).oppgaveId, is(oppgaveliste.get(1).getOppgaveId()));
    }
    @Test
    void skalLeggeTilbakeTilordnetOppgaveUtenTilgang() throws HentOppgaveOppgaveIkkeFunnet {
        List<WSOppgave> oppgaveliste = Arrays.asList(
                lagWSOppgave().withOppgaveId("1").withGjelder( new WSBruker().withBrukerId("10108000398")),
                lagWSOppgave().withOppgaveId("2").withGjelder( new WSBruker().withBrukerId("10108000398"))
        );
        when(tilgangskontrollContext.checkAbac(any(AbacRequest.class))).thenReturn(
                new AbacResponse(singletonList(new Response(Decision.Deny,emptyList())))
        );
        when(oppgaveWS.finnOppgaveListe(any(WSFinnOppgaveListeRequest.class)))
                .thenReturn(new WSFinnOppgaveListeResponse()
                        .withOppgaveListe(oppgaveliste)
                        .withTotaltAntallTreff(oppgaveliste.size()));
        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(mockHentOppgaveResponseMedTilordning());

        List<Oppgave> resultat = SubjectHandlerUtil.withIdent("Z999999", () -> oppgaveBehandlingService.finnTildelteOppgaverIGsak());


                assertThat(resultat.size(), is(0));
        //assertThat(resultat.get(0).oppgaveId, is(oppgaveliste.get(0).getOppgaveId()));
        //assertThat(resultat.get(1).oppgaveId, is(oppgaveliste.get(1).getOppgaveId()));
    }

}
