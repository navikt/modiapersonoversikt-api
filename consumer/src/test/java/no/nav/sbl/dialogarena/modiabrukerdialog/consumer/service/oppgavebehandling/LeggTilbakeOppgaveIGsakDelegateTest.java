package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.LeggTilbakeOppgaveIGsakRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.SubjectHandlerUtil;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.arbeidsfordeling.ArbeidsfordelingV1ServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollMock;
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnOppgaveListeRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnOppgaveListeResponse;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveResponse;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSEndreOppgave;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSLagreOppgaveRequest;
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.TildelOppgaveV1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.OppgaveMockFactory.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.OppgaveMockFactory.mockFinnOppgaveListe;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LeggTilbakeOppgaveIGsakDelegateTest {

    public static final String VALGT_ENHET = "4300";

    private OppgaveV3 oppgaveServiceMock;
    private AnsattService ansattServiceMock;
    private OppgavebehandlingV3 oppgavebehandlingMock;
    private TildelOppgaveV1 tildelOppgaveMock;
    private ArbeidsfordelingV1Service arbeidsfordelingMock;
    private Tilgangskontroll tilgangskontroll;

    private OppgaveBehandlingServiceImpl oppgaveBehandlingService;

    @BeforeEach
    void before() {
        mockTjenester();
        oppgaveBehandlingService = new OppgaveBehandlingServiceImpl(oppgavebehandlingMock, tildelOppgaveMock, oppgaveServiceMock, ansattServiceMock, arbeidsfordelingMock, tilgangskontroll);
    }

    private void mockTjenester() {
        oppgaveServiceMock = mock(OppgaveV3.class);
        ansattServiceMock = mockAnsattService();
        oppgavebehandlingMock = mock(OppgavebehandlingV3.class);
        tildelOppgaveMock = mock(TildelOppgaveV1.class);
        arbeidsfordelingMock = mock(ArbeidsfordelingV1ServiceImpl.class);
        tilgangskontroll = TilgangskontrollMock.get();
    }

    private AnsattService mockAnsattService() {
        AnsattService ansattServiceMock = mock(AnsattService.class);
        when(ansattServiceMock.hentAnsattNavn(anyString())).thenReturn(OppgaveMockFactory.ANSVARLIG_SAKSBEHANDLER);
        return ansattServiceMock;
    }

    @Test
    void skalLeggeTilbakeOppgaveIGsakUtenEndretTemagruppe() throws HentOppgaveOppgaveIkkeFunnet,
            LagreOppgaveOptimistiskLasing, LagreOppgaveOppgaveIkkeFunnet {
        WSHentOppgaveResponse hentOppgaveResponse = mockHentOppgaveResponseMedTilordning();
        WSFinnOppgaveListeResponse finnOppgaveListeResponse = mockFinnOppgaveListe();
        when(oppgaveServiceMock.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(hentOppgaveResponse);
        when(oppgaveServiceMock.finnOppgaveListe(any(WSFinnOppgaveListeRequest.class))).thenReturn(finnOppgaveListeResponse);
        String opprinneligBeskrivelse = hentOppgaveResponse.getOppgave().getBeskrivelse();

        ArgumentCaptor<WSLagreOppgaveRequest> lagreOppgaveRequestCaptor = ArgumentCaptor.forClass(WSLagreOppgaveRequest.class);

        SubjectHandlerUtil.withIdent(ANSVARLIG_SAKSBEHANDLER, () -> oppgaveBehandlingService.leggTilbakeOppgaveIGsak(lagLeggTilbakeNyTemagruppeRequest()));

        verify(oppgavebehandlingMock, times(6)).lagreOppgave(lagreOppgaveRequestCaptor.capture());
        WSEndreOppgave endreOriginalOppgave = lagreOppgaveRequestCaptor.getAllValues().get(0).getEndreOppgave();
        assertThat(endreOriginalOppgave.getAnsvarligId(), is(""));
        assertThat(endreOriginalOppgave.getBeskrivelse(), containsString("\n" + opprinneligBeskrivelse));
        assertThat(endreOriginalOppgave.getBeskrivelse(), containsString("\n" + "nyBeskrivelse"));
        assertThat(endreOriginalOppgave.getFagomradeKode(), is("ARBD_KNA"));
        assertThat(endreOriginalOppgave.getUnderkategoriKode(), is("FMLI_KNA"));
        assertThat(endreOriginalOppgave.getAnsvarligEnhetId(), is(hentOppgaveResponse.getOppgave().getAnsvarligEnhetId()));

        lagreOppgaveRequestCaptor.getAllValues()
                .stream()
                .skip(1)
                .map(WSLagreOppgaveRequest::getEndreOppgave)
                .forEach((oppgaveSomErLagtTilbake) -> {
                    assertThat(oppgaveSomErLagtTilbake.getAnsvarligId(), is(""));
                    assertThat(oppgaveSomErLagtTilbake.getBeskrivelse(), containsString("\nbeskrivelse"));
                    assertThat(oppgaveSomErLagtTilbake.getBeskrivelse(), containsString("\nnyBeskrivelse"));
                    assertThat(oppgaveSomErLagtTilbake.getFagomradeKode(), is("ARBD_KNA"));
                    assertThat(oppgaveSomErLagtTilbake.getUnderkategoriKode(), is("ARBEID_HJE"));
                    assertThat(oppgaveSomErLagtTilbake.getAnsvarligEnhetId(), is("ansvarligenhetid"));
                });
    }

    @Test
    void skalLeggeTilbakeOppgaveIGsakMedEndretTemagruppe() throws HentOppgaveOppgaveIkkeFunnet,
            LagreOppgaveOptimistiskLasing, LagreOppgaveOppgaveIkkeFunnet {
        when(oppgaveServiceMock.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(mockHentOppgaveResponseMedTilordning());
        when(oppgaveServiceMock.finnOppgaveListe(any(WSFinnOppgaveListeRequest.class))).thenReturn(mockFinnOppgaveListe());

        String nyEnhetId = "4100";

        when(arbeidsfordelingMock.finnBehandlendeEnhetListe(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList(new AnsattEnhet(nyEnhetId, null)));

        String opprinneligBeskrivelse = mockHentOppgaveResponseMedTilordning().getOppgave().getBeskrivelse();

        ArgumentCaptor<WSLagreOppgaveRequest> lagreOppgaveRequestCaptor = ArgumentCaptor.forClass(WSLagreOppgaveRequest.class);
        SubjectHandlerUtil.withIdent(ANSVARLIG_SAKSBEHANDLER, () -> oppgaveBehandlingService.leggTilbakeOppgaveIGsak(lagLeggTilbakeNyTemagruppeRequest()));

        verify(oppgavebehandlingMock, times(6)).lagreOppgave(lagreOppgaveRequestCaptor.capture());
        WSEndreOppgave endreOriginalOppgave = lagreOppgaveRequestCaptor.getAllValues().get(0).getEndreOppgave();
        assertThat(endreOriginalOppgave.getAnsvarligId(), is(""));
        assertThat(endreOriginalOppgave.getBeskrivelse(), containsString("\n" + opprinneligBeskrivelse));
        assertThat(endreOriginalOppgave.getUnderkategoriKode(), is("FMLI_KNA"));
        assertThat(endreOriginalOppgave.getAnsvarligEnhetId(), is(nyEnhetId));

        lagreOppgaveRequestCaptor.getAllValues()
                .stream()
                .skip(1)
                .map(WSLagreOppgaveRequest::getEndreOppgave)
                .forEach((oppgaveSomErLagtTilbake) -> {
                    assertThat(oppgaveSomErLagtTilbake.getAnsvarligId(), is(""));
                    assertThat(oppgaveSomErLagtTilbake.getBeskrivelse(), containsString("\nbeskrivelse"));
                    assertThat(oppgaveSomErLagtTilbake.getBeskrivelse(), containsString("\nnyBeskrivelse"));
                    assertThat(oppgaveSomErLagtTilbake.getFagomradeKode(), is("ARBD_KNA"));
                    assertThat(oppgaveSomErLagtTilbake.getUnderkategoriKode(), is("ARBEID_HJE"));
                    assertThat(oppgaveSomErLagtTilbake.getAnsvarligEnhetId(), is("ansvarligenhetid"));
                });
    }

    @Test
    @DisplayName("Sjekker om innlogget saksbehandler er samme som saksbehandler som er ansvarlig for oppgaven i GSAK")
    void skalKasteFeilOmSaksbehandlerIkkeHarTilgangTilOppgave() throws HentOppgaveOppgaveIkkeFunnet {
        when(oppgaveServiceMock.hentOppgave(any()))
                .thenReturn(new WSHentOppgaveResponse().withOppgave(lagWSOppgave().withAnsvarligId("ANNEN_SAKSBEHANDLER")));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                SubjectHandlerUtil.withIdent(ANSVARLIG_SAKSBEHANDLER, () ->
                        oppgaveBehandlingService.leggTilbakeOppgaveIGsak(lagLeggTilbakeNyTemagruppeRequest()
                        )
                )
        );
        assertThat(exception.getStatus(), is(HttpStatus.FORBIDDEN));
    }

    private LeggTilbakeOppgaveIGsakRequest lagLeggTilbakeNyTemagruppeRequest() {
        String nyBeskrivelse = "nyBeskrivelse";
        return new LeggTilbakeOppgaveIGsakRequest()
                .withSaksbehandlersValgteEnhet(VALGT_ENHET)
                .withTemagruppe(Temagruppe.FMLI)
                .withOppgaveId(OPPGAVE_ID)
                .withBeskrivelse(nyBeskrivelse);
    }
}
