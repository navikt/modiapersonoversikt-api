package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

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

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GsakServiceTest {

    private static final String FNR = "11111111";
    private static final String SAK_ID = "saksid";
    private static final String TEMA = "tema";
    private static final String SAKSTYPE = "sakstype";
    private static final String FAGSYSTEM = "fagsystem";
    private static final DateTime OPPRETTET_DATO = DateTime.now();

    @Captor
    private ArgumentCaptor<WSOpprettOppgaveRequest> wsOpprettOppgaveRequestArgumentCaptor;

    @Mock
    private OppgavebehandlingV3 oppgavebehandling;
    @Mock
    private no.nav.virksomhet.tjenester.sak.v1.Sak sakWs;

    @InjectMocks
    private GsakService gsakService;

    private WSGenerellSak wsGenerellSak;

    @Before
    public void setUp() {
        wsGenerellSak = new WSGenerellSak()
                .withSakId(SAK_ID)
                .withFagomradeKode(TEMA)
                .withSakstypeKode(SAKSTYPE)
                .withFagsystemKode(FAGSYSTEM)
                .withEndringsinfo(new WSEndringsinfo().withOpprettetDato(OPPRETTET_DATO));

        when(sakWs.finnGenerellSakListe(any(WSFinnGenerellSakListeRequest.class)))
                .thenReturn(new WSFinnGenerellSakListeResponse().withSakListe(wsGenerellSak));
    }

    @Test
    public void skalTransformereResponseTilSaksliste() {
        List<Sak> saksliste = gsakService.hentSakerForBruker(FNR);

        assertThat(saksliste.get(0).saksId, is(SAK_ID));
    }

    @Test
    public void skalTransformasjonenSkalGenerereRelevanteFelter() {
        Sak sak = GsakService.tilSak.transform(wsGenerellSak);

        assertThat(sak.saksId, is(SAK_ID));
        assertThat(sak.tema, is(TEMA));
        assertThat(sak.sakstype, is(SAKSTYPE));
        assertThat(sak.fagsystem, is(FAGSYSTEM));
        assertThat(sak.opprettetDato, is(OPPRETTET_DATO));
    }

    @Test
    public void skalSendeKallOmOpprettelseAvOppgaveMedRiktigeFelter() {
        NyOppgave nyOppgave = new NyOppgave();
        nyOppgave.beskrivelse = "beskrivelse";
        nyOppgave.enhet = "enhetsnavn";
        nyOppgave.prioritet = "prioritet";
        nyOppgave.tema = "tema";
        nyOppgave.type = "type";

        gsakService.opprettGsakOppgave(nyOppgave);

        verify(oppgavebehandling).opprettOppgave(wsOpprettOppgaveRequestArgumentCaptor.capture());
        WSOpprettOppgaveRequest request = wsOpprettOppgaveRequestArgumentCaptor.getValue();

        assertThat(request.getOpprettetAvEnhetId(), is(GsakService.OPPRETTET_AV_ENHET_ID));
        assertThat(request.getOpprettOppgave().getAnsvarligEnhetId(), is(nyOppgave.enhet));
        assertThat(request.getOpprettOppgave().getBeskrivelse(), is(nyOppgave.beskrivelse));
        assertThat(request.getOpprettOppgave().getFagomradeKode(), is(nyOppgave.tema));
        assertThat(request.getOpprettOppgave().getOppgavetypeKode(), is(nyOppgave.type));
        assertThat(request.getOpprettOppgave().getPrioritetKode(), is(nyOppgave.prioritet));
        assertThat(request.getOpprettOppgave().isLest(), is(false));
    }
}
