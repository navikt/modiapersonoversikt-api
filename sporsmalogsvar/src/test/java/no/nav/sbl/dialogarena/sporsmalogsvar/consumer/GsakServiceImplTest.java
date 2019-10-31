package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSGeneriskfMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg;
import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.common.auth.SsoToken;
import no.nav.common.auth.Subject;
import no.nav.common.auth.SubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.NyOppgave;
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
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettOppgaveRequest;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.GsakKodeTema.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class GsakServiceImplTest {

    private static final String JOURNALFORENDE_ENHET = "2222";
    public static final Subject TEST_SUBJECT = new Subject("Z999999", IdentType.InternBruker, SsoToken.oidcToken("token", emptyMap()));

    @Captor
    private ArgumentCaptor<WSOpprettOppgaveRequest> wsOpprettOppgaveRequestArgumentCaptor;

    @Captor
    private ArgumentCaptor<WSLagreOppgaveRequest> wsLagreOppgaveRequestArgumentCaptor;

    @Mock
    private OppgavebehandlingV3 oppgavebehandling;
    @Mock
    private AnsattService ansattWS;
    @Mock
    private OppgaveV3 oppgaveWS;

    @InjectMocks
    private GsakServiceImpl gsakService;

    @BeforeEach
    public void setUp() {
        initMocks(this);
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
        WSOppgave wsOppgave = opprettWSOppgave(kodeFagomrade, kodeStatus);
        wsHentOppgaveResponse.setOppgave(wsOppgave);
        return wsHentOppgaveResponse;
    }

    private WSOppgave opprettWSOppgave(String kodeFagomrade, String kodeStatus) {
        WSOppgave wsOppgave = new WSOppgave();
        settFagomradeMedKodeForOppgave(kodeFagomrade, wsOppgave);
        settStatusMedKodeForOppgave(kodeStatus, wsOppgave);
        return wsOppgave;
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
    public void skalKunneFerdigstilleGsakOppgave() throws GsakService.OppgaveErFerdigstilt, LagreOppgaveOptimistiskLasing, LagreOppgaveOppgaveIkkeFunnet {
        String testBeskrivelse = "Dette er en test beskrivelse";
        WSOppgave wsOppgave = opprettWSOppgave("XXX", "YYY");

        leggTilResterendeOppgaveProperties(wsOppgave);
        SubjectHandler.withSubject(TEST_SUBJECT, () -> gsakService.ferdigstillGsakOppgave(JOURNALFORENDE_ENHET, wsOppgave, testBeskrivelse));

        verify(oppgavebehandling).lagreOppgave(wsLagreOppgaveRequestArgumentCaptor.capture());

        WSEndreOppgave wsEndreOppgave = wsLagreOppgaveRequestArgumentCaptor.getValue().getEndreOppgave();
        Integer id = wsLagreOppgaveRequestArgumentCaptor.getValue().getEndretAvEnhetId();

        assertThat(wsEndreOppgave.getBeskrivelse(), containsString(testBeskrivelse));
        assertThat(wsEndreOppgave.getBeskrivelse(), containsString(JOURNALFORENDE_ENHET));
        assertThat(id, is(Integer.parseInt(JOURNALFORENDE_ENHET)));
    }

    private void leggTilResterendeOppgaveProperties(WSOppgave wsOppgave) {
        wsOppgave.setOppgaveId("111");
        wsOppgave.setAnsvarligId("111");

        WSBruker wsBruker = new WSBruker();
        wsBruker.setBrukerId("333");
        wsBruker.setBrukertypeKode("3333");
        wsOppgave.setGjelder(wsBruker);

        wsOppgave.setDokumentId("444");
        wsOppgave.setKravId("555");
        wsOppgave.setAnsvarligId("666");

        WSOppgavetype wsOppgavetype = new WSOppgavetype();
        wsOppgavetype.setKode("777");
        wsOppgave.setOppgavetype(wsOppgavetype);

        WSPrioritet wsPrioritet = new WSPrioritet();
        wsPrioritet.setKode("888");
        wsOppgave.setPrioritet(wsPrioritet);

        WSUnderkategori wsUnderkategori = new WSUnderkategori();
        wsUnderkategori.setKode("999");
        wsOppgave.setUnderkategori(wsUnderkategori);

        wsOppgave.setAktivFra(new LocalDate(1, 1, 1));
        wsOppgave.setVersjon(0);
        wsOppgave.setSaksnummer("1");
        wsOppgave.setLest(true);
    }

    @Test
    public void senderKallOmOpprettelseAvOppgaveMedRiktigeFelter() {
        NyOppgave nyOppgave = createNyOppgave();

        SubjectHandler.withSubject(TEST_SUBJECT, () -> gsakService.opprettGsakOppgave(JOURNALFORENDE_ENHET, nyOppgave));


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

        SubjectHandler.withSubject(TEST_SUBJECT, () -> gsakService.opprettGsakOppgave(JOURNALFORENDE_ENHET, nyOppgave));

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
        nyOppgave.prioritet = new Prioritet("tema", "");
        nyOppgave.type = new OppgaveType("type", "", 0);
        nyOppgave.tema = new Tema("tema", "", asList(nyOppgave.type), asList(nyOppgave.prioritet), asList(new Underkategori("", "")));
        nyOppgave.underkategori = new Underkategori("underkategori", "");
        nyOppgave.henvendelseId = "henvendelseId";
        nyOppgave.brukerId = "11111111111";
        return nyOppgave;
    }

    @Test
    public void henterJournalforendeEnhetFraSaksbehandlerInnstillinger() {
        NyOppgave nyOppgave = createNyOppgave();

        SubjectHandler.withSubject(TEST_SUBJECT, () -> gsakService.opprettGsakOppgave(JOURNALFORENDE_ENHET, nyOppgave));

        verify(oppgavebehandling).opprettOppgave(wsOpprettOppgaveRequestArgumentCaptor.capture());
        WSOpprettOppgaveRequest request = wsOpprettOppgaveRequestArgumentCaptor.getValue();

        assertThat(request.getOpprettetAvEnhetId(), is(Integer.parseInt(JOURNALFORENDE_ENHET)));
    }

    @Test
    public void setterDefaultJournalforendeEnhetDersomIntegerParsingFeilerForEnhetId() {
        NyOppgave nyOppgave = createNyOppgave();

        SubjectHandler.withSubject(TEST_SUBJECT, () -> gsakService.opprettGsakOppgave("asdaasd", nyOppgave));

        verify(oppgavebehandling).opprettOppgave(wsOpprettOppgaveRequestArgumentCaptor.capture());
        WSOpprettOppgaveRequest request = wsOpprettOppgaveRequestArgumentCaptor.getValue();

        assertThat(request.getOpprettetAvEnhetId(), is(GsakServiceImpl.DEFAULT_OPPRETTET_AV_ENHET_ID));

    }

}
