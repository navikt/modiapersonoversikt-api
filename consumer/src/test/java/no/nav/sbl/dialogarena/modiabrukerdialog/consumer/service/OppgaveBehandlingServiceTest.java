package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSGeneriskfMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg;
import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SaksbehandlerInnstillingerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.*;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.*;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.*;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.*;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSEnhet;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.*;
import no.nav.virksomhet.tjenester.ruting.v1.Ruting;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService.ANTALL_PLUKK_FORSOK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService.ENHET;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService.FikkIkkeTilordnet;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {OppgaveTestConfig.class})
public class OppgaveBehandlingServiceTest {

    @Captor
    ArgumentCaptor<WSFinnOppgaveListeRequest> finnOppgaveListeRequestCaptor;
    @Captor
    ArgumentCaptor<WSFerdigstillOppgaveBolkRequest> ferdigstillOppgaveBolkRequestCaptor;
    @Captor
    ArgumentCaptor<WSLagreOppgaveRequest> lagreOppgaveRequestCaptor;

    @Mock
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Mock
    private AnsattService ansattWS;
    @Mock
    private Ruting rutingWS;

    @Inject
    private OppgaveV3 oppgaveWS;
    @Inject
    private OppgavebehandlingV3 oppgavebehandlingWS;

    @InjectMocks
    private OppgaveBehandlingService oppgaveBehandlingService;

    @Before
    public void init() {
        System.setProperty(StaticSubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
        initMocks(this);
    }

    @Test
    public void skalHenteSporsmaalOgTilordneIGsak() throws HentOppgaveOppgaveIkkeFunnet, LagreOppgaveOppgaveIkkeFunnet, LagreOppgaveOptimistiskLasing, FikkIkkeTilordnet {
        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(mockHentOppgaveResponse());

        oppgaveBehandlingService.tilordneOppgaveIGsak("oppgaveid");

        verify(oppgavebehandlingWS).lagreOppgave(lagreOppgaveRequestCaptor.capture());
        WSLagreOppgaveRequest request = lagreOppgaveRequestCaptor.getValue();

        assertThat(request.getEndreOppgave().getAnsvarligId(), is(SubjectHandler.getSubjectHandler().getUid()));
        assertThat(request.getEndretAvEnhetId(), is(ENHET));
    }

    @Test
    public void skalPlukkeOppgaveFraGsak() {
        WSFinnOppgaveListeResponse finnOppgaveListeResponse = new WSFinnOppgaveListeResponse();
        finnOppgaveListeResponse.getOppgaveListe().add(lagWSOppgave());
        when(oppgaveWS.finnOppgaveListe(any(WSFinnOppgaveListeRequest.class))).thenReturn(finnOppgaveListeResponse);

        oppgaveBehandlingService.plukkOppgaveFraGsak(Temagruppe.ARBD);
        verify(oppgaveWS).finnOppgaveListe(finnOppgaveListeRequestCaptor.capture());
        assertThat(finnOppgaveListeRequestCaptor.getValue().getSok().getFagomradeKodeListe().get(0), is("KNA"));
        assertThat(finnOppgaveListeRequestCaptor.getValue().getFilter().getMaxAntallSvar(), is(0));
        assertThat(finnOppgaveListeRequestCaptor.getValue().getFilter().isUfordelte(), is(true));
    }

    @Test
    public void skalPlukkeNyOppgaveHvisTilordningFeiler() throws LagreOppgaveOptimistiskLasing, LagreOppgaveOppgaveIkkeFunnet {
        WSFinnOppgaveListeResponse finnOppgaveListeResponse = new WSFinnOppgaveListeResponse();
        finnOppgaveListeResponse.getOppgaveListe().add(lagWSOppgave());
        when(oppgaveWS.finnOppgaveListe(any(WSFinnOppgaveListeRequest.class))).thenReturn(finnOppgaveListeResponse);
        doThrow(LagreOppgaveOptimistiskLasing.class).doNothing().when(oppgavebehandlingWS).lagreOppgave(any(WSLagreOppgaveRequest.class));

        oppgaveBehandlingService.plukkOppgaveFraGsak(Temagruppe.ARBD);
        verify(oppgaveWS, times(2)).finnOppgaveListe(any(WSFinnOppgaveListeRequest.class));
        verify(oppgavebehandlingWS, times(2)).lagreOppgave(any(WSLagreOppgaveRequest.class));
    }

    @Test
    public void skalIkkePlukkeEvigOmIngenOppgaverKanTilordnes() throws LagreOppgaveOptimistiskLasing, LagreOppgaveOppgaveIkkeFunnet {
        WSFinnOppgaveListeResponse finnOppgaveListeResponse = new WSFinnOppgaveListeResponse();
        finnOppgaveListeResponse.getOppgaveListe().add(lagWSOppgave());
        when(oppgaveWS.finnOppgaveListe(any(WSFinnOppgaveListeRequest.class))).thenReturn(finnOppgaveListeResponse);
        doThrow(LagreOppgaveOptimistiskLasing.class).when(oppgavebehandlingWS).lagreOppgave(any(WSLagreOppgaveRequest.class));

        oppgaveBehandlingService.plukkOppgaveFraGsak(Temagruppe.ARBD);
        verify(oppgaveWS, times(ANTALL_PLUKK_FORSOK)).finnOppgaveListe(any(WSFinnOppgaveListeRequest.class));
        verify(oppgavebehandlingWS, times(ANTALL_PLUKK_FORSOK)).lagreOppgave(any(WSLagreOppgaveRequest.class));
    }

    @Test
    public void skalFerdigstilleOppgaveFraGsak() throws HentOppgaveOppgaveIkkeFunnet, HentNAVAnsattFaultGOSYSGeneriskfMsg, HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg {
        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(mockHentOppgaveResponse());
        when(ansattWS.hentAnsattNavn(anyString())).thenReturn("");

        oppgaveBehandlingService.ferdigstillOppgaveIGsak("1");
        verify(oppgavebehandlingWS).ferdigstillOppgaveBolk(ferdigstillOppgaveBolkRequestCaptor.capture());
        assertThat(ferdigstillOppgaveBolkRequestCaptor.getValue().getOppgaveIdListe().get(0), is("1"));
    }

    @Test
    public void skalLeggeTilbakeOppgaveIGsakUtenEndretTemagruppe()
            throws LagreOppgaveOppgaveIkkeFunnet, HentOppgaveOppgaveIkkeFunnet, LagreOppgaveOptimistiskLasing, HentNAVAnsattFaultGOSYSGeneriskfMsg, HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg {

        WSHentOppgaveResponse hentOppgaveResponse = mockHentOppgaveResponseMedTilordning();
        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(hentOppgaveResponse);
        when(ansattWS.hentAnsattNavn(anyString())).thenReturn("");

        String nyBeskrivelse = "nyBeskrivelse";
        String opprinneligBeskrivelse = hentOppgaveResponse.getOppgave().getBeskrivelse();
        oppgaveBehandlingService.leggTilbakeOppgaveIGsak(optional("1"), nyBeskrivelse, Optional.<Temagruppe>none());

        verify(oppgavebehandlingWS).lagreOppgave(lagreOppgaveRequestCaptor.capture());
        WSEndreOppgave endreOppgave = lagreOppgaveRequestCaptor.getValue().getEndreOppgave();
        assertThat(endreOppgave.getAnsvarligId(), is(""));
        assertThat(endreOppgave.getBeskrivelse(), containsString(opprinneligBeskrivelse + "\n"));
        assertThat(endreOppgave.getFagomradeKode(), is("ARBD_KNA"));
        assertThat(endreOppgave.getAnsvarligEnhetId(), is(hentOppgaveResponse.getOppgave().getAnsvarligEnhetId()));
    }

    @Test
    public void skalLeggeTilbakeOppgaveIGsakMedEndretTemagruppe()
            throws LagreOppgaveOppgaveIkkeFunnet, HentOppgaveOppgaveIkkeFunnet, LagreOppgaveOptimistiskLasing, HentNAVAnsattFaultGOSYSGeneriskfMsg, HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg {

        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(mockHentOppgaveResponseMedTilordning());
        when(ansattWS.hentAnsattNavn(anyString())).thenReturn("");

        String nyEnhet = "4100";
        when(rutingWS.finnAnsvarligEnhetForOppgavetype(any(WSFinnAnsvarligEnhetForOppgavetypeRequest.class)))
                .thenReturn(new WSFinnAnsvarligEnhetForOppgavetypeResponse().withEnhetListe(asList(new WSEnhet().withEnhetId(nyEnhet))));

        String nyBeskrivelse = "nyBeskrivelse";
        String opprinneligBeskrivelse = mockHentOppgaveResponseMedTilordning().getOppgave().getBeskrivelse();
        oppgaveBehandlingService.leggTilbakeOppgaveIGsak(optional("1"), nyBeskrivelse, optional(Temagruppe.FMLI));

        verify(oppgavebehandlingWS).lagreOppgave(lagreOppgaveRequestCaptor.capture());
        WSEndreOppgave endreOppgave = lagreOppgaveRequestCaptor.getValue().getEndreOppgave();
        assertThat(endreOppgave.getAnsvarligId(), is(""));
        assertThat(endreOppgave.getBeskrivelse(), containsString(opprinneligBeskrivelse + "\n"));
        assertThat(endreOppgave.getUnderkategoriKode(), is("FMLI_KNA"));
        assertThat(endreOppgave.getAnsvarligEnhetId(), is(nyEnhet));
    }

    @Test
    public void systemetLeggerTilbakeOppgaveIGsakUtenEndringer() throws HentOppgaveOppgaveIkkeFunnet, LagreOppgaveOptimistiskLasing, LagreOppgaveOppgaveIkkeFunnet {
        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(mockHentOppgaveResponseMedTilordning());

        oppgaveBehandlingService.systemLeggTilbakeOppgaveIGsak("1");

        verify(oppgavebehandlingWS).lagreOppgave(lagreOppgaveRequestCaptor.capture());
        WSEndreOppgave endreOppgave = lagreOppgaveRequestCaptor.getValue().getEndreOppgave();
        assertThat(endreOppgave.getBeskrivelse(), is(mockHentOppgaveResponseMedTilordning().getOppgave().getBeskrivelse()));
    }

    private WSHentOppgaveResponse mockHentOppgaveResponse() {
        return new WSHentOppgaveResponse().withOppgave(lagWSOppgave());
    }

    private WSHentOppgaveResponse mockHentOppgaveResponseMedTilordning() {
        return new WSHentOppgaveResponse().withOppgave(lagWSOppgave().withAnsvarligId("id").withBeskrivelse("opprinnelig beskrivelse"));
    }

    @Test
    public void skalKonvertereFraWSOppgaveTilWSEndreOppgave() {
        WSOppgave oppgave = lagWSOppgave();

        WSEndreOppgave endreOppgave = OppgaveBehandlingService.tilWSEndreOppgave(oppgave);

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

    public static WSOppgave lagWSOppgave() {
        return new WSOppgave()
                .withOppgaveId("oppgaveid")
                .withAnsvarligId("ansvarligid")
                .withGjelder(new WSBruker().withBrukerId("***REMOVED***").withBrukertypeKode("brukertypekode"))
                .withDokumentId("dokumentid")
                .withKravId("kravid")
                .withAnsvarligEnhetId("ansvarligenhetid")

                .withFagomrade(new WSFagomrade().withKode("ARBD_KNA"))
                .withOppgavetype(new WSOppgavetype().withKode("wsOppgavetype"))
                .withPrioritet(new WSPrioritet().withKode("NORM_GEN"))
                .withUnderkategori(new WSUnderkategori().withKode("ARBEID_HJE"))

                .withAktivFra(now().toLocalDate())
                .withBeskrivelse("beskrivelse")
                .withVersjon(1)
                .withSaksnummer("saksnummer")
                .withStatus(new WSStatus().withKode("statuskode"))
                .withLest(false);
    }

}
