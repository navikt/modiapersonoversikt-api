package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSGeneriskfMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg;
import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.*;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnOppgaveListeRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnOppgaveListeResponse;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveResponse;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.TildelOppgaveUgyldigInput;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.*;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSEnhet;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSFinnAnsvarligEnhetForOppgavetypeRequest;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSFinnAnsvarligEnhetForOppgavetypeResponse;
import no.nav.virksomhet.tjenester.ruting.v1.Ruting;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingServiceImpl.ANTALL_PLUKK_FORSOK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingServiceImpl.DEFAULT_ENHET;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OppgaveBehandlingServiceImplTest {

    @Captor
    ArgumentCaptor<WSFinnOppgaveListeRequest> finnOppgaveListeRequestCaptor;
    @Captor
    ArgumentCaptor<WSFerdigstillOppgaveBolkRequest> ferdigstillOppgaveBolkRequestCaptor;
    @Captor
    ArgumentCaptor<WSLagreOppgaveRequest> lagreOppgaveRequestCaptor;
    @Captor
    ArgumentCaptor<WSTildelOppgaveRequest> tildelOppgaveRequestCaptor;
    @Captor
    ArgumentCaptor<WSHentOppgaveRequest> hentOppgaveRequestCaptor;

    @Mock
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Mock
    private AnsattService ansattWS;
    @Mock
    private Ruting rutingWS;
    @Mock
    private OppgaveV3 oppgaveWS;
    @Mock
    private OppgavebehandlingV3 oppgavebehandlingWS;

    @InjectMocks
    private OppgaveBehandlingServiceImpl oppgaveBehandlingService;

    @Before
    public void init() {
        System.setProperty(StaticSubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
    }

    @Test
    public void skalHenteSporsmaalOgTilordneIGsak() throws HentOppgaveOppgaveIkkeFunnet, LagreOppgaveOppgaveIkkeFunnet, LagreOppgaveOptimistiskLasing, OppgaveBehandlingService.FikkIkkeTilordnet {
        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(mockHentOppgaveResponse());

        oppgaveBehandlingService.tilordneOppgaveIGsak("oppgaveid", Temagruppe.ARBD);

        verify(oppgavebehandlingWS).lagreOppgave(lagreOppgaveRequestCaptor.capture());
        WSLagreOppgaveRequest request = lagreOppgaveRequestCaptor.getValue();

        assertThat(request.getEndreOppgave().getAnsvarligId(), is(SubjectHandler.getSubjectHandler().getUid()));
        assertThat(request.getEndretAvEnhetId(), is(DEFAULT_ENHET));
    }

    @Test
    public void skalPlukkeOppgaveFraGsak() throws TildelOppgaveUgyldigInput, HentOppgaveOppgaveIkkeFunnet {
        WSTildelOppgaveResponse tildelOppgaveResponse = new WSTildelOppgaveResponse();
        tildelOppgaveResponse.setOppgaveId(lagWSOppgave().getOppgaveId());

        WSHentOppgaveResponse hentOppgaveResponse = new WSHentOppgaveResponse();
        hentOppgaveResponse.setOppgave(lagWSOppgave());

        when(oppgavebehandlingWS.tildelOppgave(any(WSTildelOppgaveRequest.class))).thenReturn(tildelOppgaveResponse);
        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(hentOppgaveResponse);
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn("4100");

        oppgaveBehandlingService.plukkOppgaveFraGsak(Temagruppe.ARBD);
        verify(oppgavebehandlingWS).tildelOppgave(tildelOppgaveRequestCaptor.capture());
        verify(oppgaveWS).hentOppgave(hentOppgaveRequestCaptor.capture());
        assertNotNull(tildelOppgaveRequestCaptor.getValue().getSok());
        assertThat(tildelOppgaveRequestCaptor.getValue().getSok().getFagomradeKodeListe().get(0), is("KNA"));
        assertThat(tildelOppgaveRequestCaptor.getValue().getFilter().getOppgavetypeKodeListe().get(0), is("SPM_OG_SVR"));
        assertThat(hentOppgaveRequestCaptor.getValue().getOppgaveId(), is(tildelOppgaveResponse.getOppgaveId()));
    }

    @Test
    public void skalFerdigstilleOppgaveFraGsak() throws HentOppgaveOppgaveIkkeFunnet, HentNAVAnsattFaultGOSYSGeneriskfMsg, HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg {
        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(mockHentOppgaveResponse());
        when(ansattWS.hentAnsattNavn(anyString())).thenReturn("");

        oppgaveBehandlingService.ferdigstillOppgaveIGsak("1", Temagruppe.ARBD);
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
        oppgaveBehandlingService.leggTilbakeOppgaveIGsak("1", nyBeskrivelse, null);

        verify(oppgavebehandlingWS).lagreOppgave(lagreOppgaveRequestCaptor.capture());
        WSEndreOppgave endreOppgave = lagreOppgaveRequestCaptor.getValue().getEndreOppgave();
        assertThat(endreOppgave.getAnsvarligId(), is(""));
        assertThat(endreOppgave.getBeskrivelse(), containsString("\n" + opprinneligBeskrivelse));
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
        oppgaveBehandlingService.leggTilbakeOppgaveIGsak("1", nyBeskrivelse, Temagruppe.FMLI);

        verify(oppgavebehandlingWS).lagreOppgave(lagreOppgaveRequestCaptor.capture());
        WSEndreOppgave endreOppgave = lagreOppgaveRequestCaptor.getValue().getEndreOppgave();
        assertThat(endreOppgave.getAnsvarligId(), is(""));
        assertThat(endreOppgave.getBeskrivelse(), containsString("\n" + opprinneligBeskrivelse));
        assertThat(endreOppgave.getUnderkategoriKode(), is("FMLI_KNA"));
        assertThat(endreOppgave.getAnsvarligEnhetId(), is(nyEnhet));
    }

    @Test
    public void systemetLeggerTilbakeOppgaveIGsakUtenEndringer() throws HentOppgaveOppgaveIkkeFunnet, LagreOppgaveOptimistiskLasing, LagreOppgaveOppgaveIkkeFunnet {
        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(mockHentOppgaveResponseMedTilordning());

        oppgaveBehandlingService.systemLeggTilbakeOppgaveIGsak("1", Temagruppe.ARBD);

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
