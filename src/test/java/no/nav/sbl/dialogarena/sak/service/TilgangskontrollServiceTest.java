package no.nav.sbl.dialogarena.sak.service;


import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentResponse;
import no.nav.tjeneste.virksomhet.journal.v1.binding.*;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.Journalpost;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.Sak;
import no.nav.tjeneste.virksomhet.sak.v1.HentSakSakIkkeFunnet;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSFagomraader;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSFagsystemer;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSakstyper;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSHentSakResponse;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Random;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TilgangskontrollServiceTest {

    @Mock
    private GSakService gSakServiceImpl;

    @Mock
    private AktoerPortType fodselnummerAktorService;

    @Mock
    private JoarkService joarkServiceImpl;

    @InjectMocks
    private TilgangskontrollService tilgangskontrollService;

    private static Random idGenerator = new Random();
    public static final String SAKSTYPE_GENERELL = "GEN";

    //TODO greiere å skrive ordentlige tester når man har alle tjenestene på plass
    @Test
    public void testSuitenFungerer() throws HentSakSakIkkeFunnet, HentAktoerIdForIdentPersonIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning, HentDokumentSikkerhetsbegrensning, HentDokumentDokumentIkkeFunnet, HentDokumentDokumentErSlettet {
        when(gSakServiceImpl.hentSak(anyString())).thenReturn(new WSHentSakResponse().withSak(createSak("DAG", DateTime.now().minusDays(5))));
        HentAktoerIdForIdentResponse hentAktoerIdForIdentResponse = new HentAktoerIdForIdentResponse();
        hentAktoerIdForIdentResponse.setAktoerId("1232131");
        when(fodselnummerAktorService.hentAktoerIdForIdent(any(HentAktoerIdForIdentRequest.class))).thenReturn(hentAktoerIdForIdentResponse);
        when(joarkServiceImpl.hentDokument(anyString(), anyString())).thenReturn(new byte[10]);
        when(joarkServiceImpl.hentJournalpost(anyString())).thenReturn(createJournalpost());

        tilgangskontrollService.harSaksbehandlerTilgangTilDokument("123123", "123213213");
    }

    private static WSSak createSak(String tema, DateTime opprettet) {
        return new WSSak()
                .withSakId("" + idGenerator.nextInt(100000000))
                .withFagsystemSakId("" + idGenerator.nextInt(100000000))
                .withFagomraade(new WSFagomraader().withValue(tema))
                .withOpprettelsetidspunkt(opprettet)
                .withSakstype(new WSSakstyper().withValue(SAKSTYPE_GENERELL))
                .withFagsystem(new WSFagsystemer().withValue("FS22"));
    }

    private Journalpost createJournalpost() {
        Journalpost journalpost = new Journalpost();
        journalpost.setJournalpostId("journalpostid");
        journalpost.setGjelderSak(createSak());
        return journalpost;
    }

    private Sak createSak() {
        Sak sak = new Sak();
        sak.setSakId("sakId");
        sak.setErFeilregistrert(false);
        return sak;
    }
}
