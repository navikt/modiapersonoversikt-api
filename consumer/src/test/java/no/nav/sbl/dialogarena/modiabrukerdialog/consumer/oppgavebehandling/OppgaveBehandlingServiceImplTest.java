package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.oppgavebehandling;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSGeneriskfMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg;
import no.nav.brukerdialog.security.context.StaticSubjectHandler;
import no.nav.brukerdialog.security.context.SubjectHandler;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveResponse;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.TildelOppgaveUgyldigInput;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.*;
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.TildelOppgaveV1;
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.WSTildelFlereOppgaverRequest;
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.WSTildelFlereOppgaverResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.stream.Collectors.toSet;
import static no.nav.sbl.dialogarena.common.collections.Collections.asSet;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.oppgavebehandling.OppgaveBehandlingServiceImpl.DEFAULT_ENHET;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.oppgavebehandling.OppgaveMockFactory.lagWSOppgave;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.oppgavebehandling.OppgaveMockFactory.mockHentOppgaveResponseMedTilordning;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Mock
    private AnsattService ansattWS;
    @Mock
    private OppgaveV3 oppgaveWS;
    @Mock
    private OppgavebehandlingV3 oppgavebehandlingWS;
    @Mock
    private TildelOppgaveV1 tildelOppgaveWS;

    @InjectMocks
    private OppgaveBehandlingServiceImpl oppgaveBehandlingService;

    private static final String OPPGAVE_ID_1 = "123";
    private static final String OPPGAVE_ID_2 = "456";

    @Before
    public void init() {
        System.setProperty(StaticSubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
    }

    @Test
    public void skalHenteSporsmaalOgTilordneIGsak() throws HentOppgaveOppgaveIkkeFunnet, LagreOppgaveOppgaveIkkeFunnet, LagreOppgaveOptimistiskLasing, OppgaveBehandlingService.FikkIkkeTilordnet {
        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(mockHentOppgaveResponse());

        oppgaveBehandlingService.tilordneOppgaveIGsak("oppgaveid", Temagruppe.ARBD, SAKSBEHANDLERS_VALGTE_ENHET);

        verify(oppgavebehandlingWS).lagreOppgave(lagreOppgaveRequestCaptor.capture());
        WSLagreOppgaveRequest request = lagreOppgaveRequestCaptor.getValue();

        assertThat(request.getEndreOppgave().getAnsvarligId(), is(SubjectHandler.getSubjectHandler().getUid()));
        assertThat(request.getEndretAvEnhetId(), is(DEFAULT_ENHET));
    }

    @Test
    public void skalPlukkeOppgaverFraGsak() throws TildelOppgaveUgyldigInput, HentOppgaveOppgaveIkkeFunnet {
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
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet())
                .thenReturn(SAKSBEHANDLERS_VALGTE_ENHET);

        oppgaveBehandlingService.plukkOppgaverFraGsak(Temagruppe.ARBD, SAKSBEHANDLERS_VALGTE_ENHET);

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

        oppgaveBehandlingService.ferdigstillOppgaveIGsak("1", Temagruppe.ARBD, SAKSBEHANDLERS_VALGTE_ENHET);
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

}
