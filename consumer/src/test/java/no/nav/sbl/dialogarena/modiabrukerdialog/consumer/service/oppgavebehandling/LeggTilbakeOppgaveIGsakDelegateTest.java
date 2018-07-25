package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling;

import no.nav.brukerdialog.security.context.SubjectHandler;
import no.nav.brukerdialog.security.context.SubjectHandlerUtils;
import no.nav.brukerdialog.security.context.ThreadLocalSubjectHandler;
import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.brukerdialog.tools.SecurityConstants;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.LeggTilbakeOppgaveIGsakRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.arbeidsfordeling.ArbeidsfordelingV1ServiceImpl;
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveResponse;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSEndreOppgave;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSLagreOppgaveRequest;
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.TildelOppgaveV1;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.ForbiddenException;
import java.util.Collections;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.OppgaveMockFactory.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LeggTilbakeOppgaveIGsakDelegateTest {

    public static final String VALGT_ENHET = "4300";

    private OppgaveV3 oppgaveServiceMock;
    private AnsattService ansattServiceMock;
    private OppgavebehandlingV3 oppgavebehandlingMock;
    private TildelOppgaveV1 tildelOppgaveMock;
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    private ArbeidsfordelingV1Service arbeidsfordelingMock;

    private OppgaveBehandlingServiceImpl oppgaveBehandlingService;

    @BeforeAll
    static void beforeAll() {
        System.setProperty(SubjectHandler.SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getCanonicalName());
        System.setProperty(SecurityConstants.SYSTEMUSER_USERNAME, "srvModiabrukerdialog");
        setInnloggetSaksbehandler();
    }

    private static void setInnloggetSaksbehandler() {
        SubjectHandlerUtils.setSubject(new SubjectHandlerUtils
                .SubjectBuilder(OppgaveMockFactory.ANSVARLIG_SAKSBEHANDLER, IdentType.EksternBruker).withAuthLevel(4).getSubject());
    }

    @BeforeEach
    void before() {
        mockTjenester();
        oppgaveBehandlingService = new OppgaveBehandlingServiceImpl(oppgavebehandlingMock, tildelOppgaveMock, oppgaveServiceMock, ansattServiceMock, arbeidsfordelingMock);
    }

    private void mockTjenester() {
        oppgaveServiceMock = mock(OppgaveV3.class);
        ansattServiceMock = mockAnsattService();
        oppgavebehandlingMock = mock(OppgavebehandlingV3.class);
        tildelOppgaveMock = mock(TildelOppgaveV1.class);
        saksbehandlerInnstillingerService = mock(SaksbehandlerInnstillingerService.class);
        arbeidsfordelingMock = mock(ArbeidsfordelingV1ServiceImpl.class);
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
        when(oppgaveServiceMock.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(hentOppgaveResponse);
        String opprinneligBeskrivelse = hentOppgaveResponse.getOppgave().getBeskrivelse();

        ArgumentCaptor<WSLagreOppgaveRequest> lagreOppgaveRequestCaptor = ArgumentCaptor.forClass(WSLagreOppgaveRequest.class);
        oppgaveBehandlingService.leggTilbakeOppgaveIGsak(lagRequest());

        verify(oppgavebehandlingMock).lagreOppgave(lagreOppgaveRequestCaptor.capture());
        WSEndreOppgave endreOppgave = lagreOppgaveRequestCaptor.getValue().getEndreOppgave();
        assertThat(endreOppgave.getAnsvarligId(), is(""));
        assertThat(endreOppgave.getBeskrivelse(), containsString("\n" + opprinneligBeskrivelse));
        assertThat(endreOppgave.getFagomradeKode(), is("ARBD_KNA"));
        assertThat(endreOppgave.getAnsvarligEnhetId(), is(hentOppgaveResponse.getOppgave().getAnsvarligEnhetId()));
    }

    @Test
    void skalLeggeTilbakeOppgaveIGsakMedEndretTemagruppe() throws HentOppgaveOppgaveIkkeFunnet,
            LagreOppgaveOptimistiskLasing, LagreOppgaveOppgaveIkkeFunnet {
        when(oppgaveServiceMock.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(mockHentOppgaveResponseMedTilordning());

        String nyEnhetId = "4100";

        when(arbeidsfordelingMock.finnBehandlendeEnhetListe(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList(new AnsattEnhet(nyEnhetId, null)));

        String nyBeskrivelse = "nyBeskrivelse";
        String opprinneligBeskrivelse = mockHentOppgaveResponseMedTilordning().getOppgave().getBeskrivelse();

        ArgumentCaptor<WSLagreOppgaveRequest> lagreOppgaveRequestCaptor = ArgumentCaptor.forClass(WSLagreOppgaveRequest.class);
        oppgaveBehandlingService.leggTilbakeOppgaveIGsak(lagRequest());

        verify(oppgavebehandlingMock).lagreOppgave(lagreOppgaveRequestCaptor.capture());
        WSEndreOppgave endreOppgave = lagreOppgaveRequestCaptor.getValue().getEndreOppgave();
        assertThat(endreOppgave.getAnsvarligId(), is(""));
        assertThat(endreOppgave.getBeskrivelse(), containsString("\n" + opprinneligBeskrivelse));
        assertThat(endreOppgave.getUnderkategoriKode(), is("FMLI_KNA"));
        assertThat(endreOppgave.getAnsvarligEnhetId(), is(nyEnhetId));
    }

    @Test
    @DisplayName("Sjekker om innlogget saksbehandler er samme som saksbehandler som er ansvarlig for oppgaven i GSAK")
    void skalKasteFeilOmSaksbehandlerIkkeHarTilgangTilOppgave() throws HentOppgaveOppgaveIkkeFunnet {
        when(oppgaveServiceMock.hentOppgave(any()))
                .thenReturn(new WSHentOppgaveResponse().withOppgave(lagWSOppgave().withAnsvarligId("ANNEN_SAKSBEHANDLER")));

        assertThrows(ForbiddenException.class, () -> {
            oppgaveBehandlingService.leggTilbakeOppgaveIGsak(lagRequest());
        });
    }

    private LeggTilbakeOppgaveIGsakRequest lagRequest() {
        String nyBeskrivelse = "nyBeskrivelse";
        return new LeggTilbakeOppgaveIGsakRequest()
                .withSaksbehandlersValgteEnhet(VALGT_ENHET)
                .withTemagruppe(Temagruppe.FMLI)
                .withOppgaveId(OPPGAVE_ID)
                .withBeskrivelse(nyBeskrivelse);
    }
}