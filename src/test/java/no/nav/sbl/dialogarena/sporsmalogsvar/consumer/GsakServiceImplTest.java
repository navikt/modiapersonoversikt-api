package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSGeneriskfMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg;
import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.GsakKodeTema;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.NyOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSFagomrade;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSStatus;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveResponse;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettOppgaveRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.lang.System.setProperty;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GsakServiceImplTest {

    private static final String JOURNALFORENDE_ENHET = "2222";

    @Captor
    private ArgumentCaptor<WSOpprettOppgaveRequest> wsOpprettOppgaveRequestArgumentCaptor;

    @Mock
    private OppgavebehandlingV3 oppgavebehandling;
    @Mock
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Mock
    private AnsattService ansattWS;
    @Mock
    private OppgaveV3 oppgaveWS;

    @InjectMocks
    private GsakServiceImpl gsakService;


    @Before
    public void setUp() throws HentNAVAnsattFaultGOSYSGeneriskfMsg, HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg {
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn(JOURNALFORENDE_ENHET);

        when(ansattWS.hentAnsattNavn(anyString())).thenReturn("");

        setProperty(StaticSubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
    }

    @Test
    public void skalIkkeKunneAvslutteOppgaveManuelltHvisOppgaveErFerdigstilt() throws HentOppgaveOppgaveIkkeFunnet {
        WSHentOppgaveResponse wsHentOppgaveResponse = oppretteOppgaveResponse("XXX", GsakServiceImpl.KODE_OPPGAVE_FERDIGSTILT);

        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(wsHentOppgaveResponse);

        boolean oppgaveKanManuelltAvsluttes = gsakService.oppgaveKanManuelltAvsluttes(any(String.class));

        verify(oppgaveWS).hentOppgave(any(WSHentOppgaveRequest.class));

        assertThat(oppgaveKanManuelltAvsluttes, is(false));
    }

    @Test
    public void skalIkkeKunneAvslutteOppgaveManuelltHvisFagomradeKontaktNAV() throws HentOppgaveOppgaveIkkeFunnet {
        WSHentOppgaveResponse wsHentOppgaveResponse = oppretteOppgaveResponse(GsakServiceImpl.KODE_KONTAKT_NAV, "YYY");

        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(wsHentOppgaveResponse);

        boolean oppgaveKanManuelltAvsluttes = gsakService.oppgaveKanManuelltAvsluttes(any(String.class));

        verify(oppgaveWS).hentOppgave(any(WSHentOppgaveRequest.class));

        assertThat(oppgaveKanManuelltAvsluttes, is(false));
    }

    @Test
    public void skalKunneAvslutteOppgaveManuelltHvisIkkeFagomradeKontaktNAVogIkkeOppgaveErFerdigstilt() throws HentOppgaveOppgaveIkkeFunnet {
        WSHentOppgaveResponse wsHentOppgaveResponse = oppretteOppgaveResponse("XXX", "YYY");

        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(wsHentOppgaveResponse);

        boolean oppgaveKanManuelltAvsluttes = gsakService.oppgaveKanManuelltAvsluttes(any(String.class));

        verify(oppgaveWS).hentOppgave(any(WSHentOppgaveRequest.class));

        assertThat(oppgaveKanManuelltAvsluttes, is(true));
    }



    private WSHentOppgaveResponse oppretteOppgaveResponse(String kodeFagomrade, String kodeStatus) {
        WSHentOppgaveResponse wsHentOppgaveResponse = new WSHentOppgaveResponse();
        WSOppgave wsOppgave = new WSOppgave();
        settFagomradeMedKodeForOppgave(kodeFagomrade, wsOppgave);
        settStatusMedKodeForOppgave(kodeStatus, wsOppgave);
        wsHentOppgaveResponse.setOppgave(wsOppgave);
        return wsHentOppgaveResponse;
    }

    private void settStatusMedKodeForOppgave(String kodeStatus, WSOppgave wsOppgave) {
        WSStatus wsStatus = new WSStatus();
        wsStatus.withKode(kodeStatus);
        wsOppgave.setStatus(wsStatus);
    }

    private void settFagomradeMedKodeForOppgave(String kodeFagomrade, WSOppgave wsOppgave) {
        WSFagomrade wsFagomrade = new WSFagomrade();
        wsFagomrade.withKode(kodeFagomrade);
        wsOppgave.setFagomrade(wsFagomrade);
    }

    @Test
    public void senderKallOmOpprettelseAvOppgaveMedRiktigeFelter() {
        NyOppgave nyOppgave = createNyOppgave();

        gsakService.opprettGsakOppgave(nyOppgave);

        verify(oppgavebehandling).opprettOppgave(wsOpprettOppgaveRequestArgumentCaptor.capture());
        WSOpprettOppgaveRequest request = wsOpprettOppgaveRequestArgumentCaptor.getValue();

        assertThat(request.getOpprettetAvEnhetId(), is(Integer.parseInt(JOURNALFORENDE_ENHET)));
        assertThat(request.getOpprettOppgave().getAnsvarligEnhetId(), is(nyOppgave.enhet.enhetId));
        assertThat(request.getOpprettOppgave().getBeskrivelse(), containsString(nyOppgave.beskrivelse));
        assertThat(request.getOpprettOppgave().getFagomradeKode(), is(nyOppgave.tema.kode));
        assertThat(request.getOpprettOppgave().getUnderkategoriKode(), is(nyOppgave.underkategori.kode));
        assertThat(request.getOpprettOppgave().getOppgavetypeKode(), is(nyOppgave.type.kode));
        assertThat(request.getOpprettOppgave().getPrioritetKode(), is(nyOppgave.prioritet.kode));
        assertThat(request.getOpprettOppgave().isLest(), is(false));
        assertThat(request.getOpprettOppgave().getHenvendelseId(), is(nyOppgave.henvendelseId));
        assertThat(request.getOpprettOppgave().getBrukerId(), is(nyOppgave.brukerId));
    }

    @Test
    public void senderKallOmOpprettelseAvOppgaveMedRiktigeFelterSelvomGjelderIkkeErSatt() {
        NyOppgave nyOppgave = createNyOppgave();
        nyOppgave.underkategori = null;

        gsakService.opprettGsakOppgave(nyOppgave);

        verify(oppgavebehandling).opprettOppgave(wsOpprettOppgaveRequestArgumentCaptor.capture());
        WSOpprettOppgaveRequest request = wsOpprettOppgaveRequestArgumentCaptor.getValue();

        assertThat(request.getOpprettetAvEnhetId(), is(Integer.parseInt(JOURNALFORENDE_ENHET)));
        assertThat(request.getOpprettOppgave().getAnsvarligEnhetId(), is(nyOppgave.enhet.enhetId));
        assertThat(request.getOpprettOppgave().getBeskrivelse(), containsString(nyOppgave.beskrivelse));
        assertThat(request.getOpprettOppgave().getFagomradeKode(), is(nyOppgave.tema.kode));
        assertThat(request.getOpprettOppgave().getUnderkategoriKode(), is(nullValue()));
        assertThat(request.getOpprettOppgave().getOppgavetypeKode(), is(nyOppgave.type.kode));
        assertThat(request.getOpprettOppgave().getPrioritetKode(), is(nyOppgave.prioritet.kode));
        assertThat(request.getOpprettOppgave().isLest(), is(false));
        assertThat(request.getOpprettOppgave().getHenvendelseId(), is(nyOppgave.henvendelseId));
        assertThat(request.getOpprettOppgave().getBrukerId(), is(nyOppgave.brukerId));
    }

    private NyOppgave createNyOppgave() {
        NyOppgave nyOppgave = new NyOppgave();
        nyOppgave.beskrivelse = "beskrivelse";
        nyOppgave.enhet = new AnsattEnhet("enhetId", "enhetNavn");
        nyOppgave.prioritet = new GsakKodeTema.Prioritet("tema", "");
        nyOppgave.type = new GsakKodeTema.OppgaveType("type", "", 0);
        nyOppgave.tema = new GsakKodeTema.Tema("tema", "", asList(nyOppgave.type), asList(nyOppgave.prioritet), asList(new GsakKodeTema.Underkategori("", "")));
        nyOppgave.underkategori = new GsakKodeTema.Underkategori("underkategori", "");
        nyOppgave.henvendelseId = "henvendelseId";
        nyOppgave.brukerId = "12345612345";
        return nyOppgave;
    }

    @Test
    public void henterJournalforendeEnhetFraSaksbehandlerInnstillinger() {
        NyOppgave nyOppgave = createNyOppgave();

        gsakService.opprettGsakOppgave(nyOppgave);

        verify(saksbehandlerInnstillingerService).getSaksbehandlerValgtEnhet();
        verify(oppgavebehandling).opprettOppgave(wsOpprettOppgaveRequestArgumentCaptor.capture());
        WSOpprettOppgaveRequest request = wsOpprettOppgaveRequestArgumentCaptor.getValue();

        assertThat(request.getOpprettetAvEnhetId(), is(Integer.parseInt(JOURNALFORENDE_ENHET)));
    }

    @Test
    public void setterDefaultJournalforendeEnhetDersomIntegerParsingFeilerForEnhetId() {
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn("er ikke mulig å parse denne til Integer");
        NyOppgave nyOppgave = createNyOppgave();

        gsakService.opprettGsakOppgave(nyOppgave);

        verify(oppgavebehandling).opprettOppgave(wsOpprettOppgaveRequestArgumentCaptor.capture());
        WSOpprettOppgaveRequest request = wsOpprettOppgaveRequestArgumentCaptor.getValue();

        assertThat(request.getOpprettetAvEnhetId(), is(GsakServiceImpl.DEFAULT_OPPRETTET_AV_ENHET_ID));

    }

}
