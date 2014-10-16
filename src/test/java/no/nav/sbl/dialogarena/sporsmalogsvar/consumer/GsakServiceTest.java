package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.GsakKodeTema;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.NyOppgave;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettOppgaveRequest;
import no.nav.virksomhet.gjennomforing.sak.v1.WSEndringsinfo;
import no.nav.virksomhet.gjennomforing.sak.v1.WSGenerellSak;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeRequest;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeResponse;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static java.lang.System.setProperty;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GsakServiceTest {

    private static final String FNR = "11111111";
    private static final String JOURNALFORENDE_ENHET = "2222";
    private static final String SAK_ID = "saksid";
    private static final String TEMA = "tema";
    private static final String SAKSTYPE = "sakstype";
    private static final String FAGSYSTEMKODE = "fagsystemkode";
    private static final DateTime OPPRETTET_DATO = DateTime.now();

    @Captor
    private ArgumentCaptor<WSOpprettOppgaveRequest> wsOpprettOppgaveRequestArgumentCaptor;

    @Mock
    private OppgavebehandlingV3 oppgavebehandling;
    @Mock
    private no.nav.virksomhet.tjenester.sak.v1.Sak sakWs;
    @Mock
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @InjectMocks
    private GsakService gsakService;

    private WSGenerellSak wsGenerellSak;

    @Before
    public void setUp() {
        wsGenerellSak = new WSGenerellSak()
                .withSakId(SAK_ID)
                .withFagomradeKode(TEMA)
                .withSakstypeKode(SAKSTYPE)
                .withFagsystemKode(FAGSYSTEMKODE)
                .withEndringsinfo(new WSEndringsinfo().withOpprettetDato(OPPRETTET_DATO));

        when(sakWs.finnGenerellSakListe(any(WSFinnGenerellSakListeRequest.class)))
                .thenReturn(new WSFinnGenerellSakListeResponse().withSakListe(wsGenerellSak));

        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn(JOURNALFORENDE_ENHET);

        setProperty(StaticSubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
    }

    @Test
    public void transformererResponseTilSaksliste() {
        List<Sak> saksliste = gsakService.hentSakerForBruker(FNR);

        assertThat(saksliste.get(0).saksId, is(SAK_ID));
    }

    @Test
    public void transformasjonenGenerererRelevanteFelter() {
        Sak sak = GsakService.TIL_SAK.transform(wsGenerellSak);

        assertThat(sak.saksId, is(SAK_ID));
        assertThat(sak.temaKode, is(TEMA));
        assertThat(sak.sakstype, is(SAKSTYPE));
        assertThat(sak.fagsystemKode, is(FAGSYSTEMKODE));
        assertThat(sak.opprettetDato, is(OPPRETTET_DATO));
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
        nyOppgave.tema = new GsakKodeTema.Tema("tema", "", Arrays.asList(nyOppgave.type), Arrays.asList(nyOppgave.prioritet));
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

        assertThat(request.getOpprettetAvEnhetId(), is(GsakService.DEFAULT_OPPRETTET_AV_ENHET_ID));

    }

}
