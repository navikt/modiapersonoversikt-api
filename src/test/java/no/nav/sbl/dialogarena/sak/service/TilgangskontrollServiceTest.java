package no.nav.sbl.dialogarena.sak.service;


import no.nav.sbl.dialogarena.sak.viewdomain.lamell.VedleggResultat;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentResponse;
import no.nav.tjeneste.virksomhet.journal.v1.binding.HentJournalpostJournalpostIkkeFunnet;
import no.nav.tjeneste.virksomhet.journal.v1.binding.HentJournalpostSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.Journalpost;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.Sak;
import no.nav.tjeneste.virksomhet.sak.v1.HentSakSakIkkeFunnet;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.*;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Random;

import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.VedleggResultat.Feilmelding.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TilgangskontrollServiceTest {

    @Mock
    private GSakService gSakService;

    @Mock
    private AktoerPortType fodselnummerAktorService;

    @Mock
    private JoarkService joarkService;

    @InjectMocks
    private TilgangskontrollService tilgangskontrollService = new TilgangskontrollServiceImpl();

    private static Random idGenerator = new Random();
    public static final String SAKSTYPE_GENERELL = "GEN";
    public static final String BRUKERS_IDENT = "12345678901";
    public static final String IKKE_BRUKERS_IDENT = "12345678902";


    @Before
    public void setup() throws HentAktoerIdForIdentPersonIkkeFunnet {
        HentAktoerIdForIdentResponse hentAktoerIdForIdentResponse = new HentAktoerIdForIdentResponse();
        hentAktoerIdForIdentResponse.setAktoerId(BRUKERS_IDENT);
        when(fodselnummerAktorService.hentAktoerIdForIdent(any(HentAktoerIdForIdentRequest.class))).thenReturn(hentAktoerIdForIdentResponse);
    }

    @Test
    public void harTilgangHvisAlleSjekkerOk() throws HentSakSakIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
        setGsakMedSakspart();
        setJournalfortOgIkkeFeilRegistrert();

        VedleggResultat vedleggResultat = tilgangskontrollService.harSaksbehandlerTilgangTilDokument("journalpostId", "fnr");
        assertTrue(vedleggResultat.harTilgang);
    }

    //TODO ignore fram til journalført er på plass på Journalpost-objektet.
    @Ignore
    @Test
    public void harIkkeTilgangHvisIkkeJournalfort() throws HentSakSakIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
        setGsakMedSakspart();
        setIkkeJournalfortOgIkkeFeilRegistrert();

        VedleggResultat vedleggResultat = tilgangskontrollService.harSaksbehandlerTilgangTilDokument("journalpostId", "fnr");
        assertFalse(vedleggResultat.harTilgang);
        assertEquals(vedleggResultat.feilmelding, IKKE_SAKSPART);
    }

    @Test
    public void harIkkeTilgangHvisIkkeSakspart() throws HentSakSakIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
        setGsakUtenSakspart();
        setJournalfortOgIkkeFeilRegistrert();

        VedleggResultat vedleggResultat = tilgangskontrollService.harSaksbehandlerTilgangTilDokument("journalpostId", "fnr");
        assertFalse(vedleggResultat.harTilgang);
        assertEquals(vedleggResultat.feilmelding, IKKE_SAKSPART);
    }

    @Test
    public void harIkkeTilgangHvisFeilRegistrert() throws HentSakSakIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
        setGsakMedSakspart();
        setJournalfortOgFeilRegistrert();

        VedleggResultat vedleggResultat = tilgangskontrollService.harSaksbehandlerTilgangTilDokument("journalpostId", "fnr");
        assertFalse(vedleggResultat.harTilgang);
        assertEquals(vedleggResultat.feilmelding, FEILREGISTRERT);
    }

    @Test
    public void harIkkeTilgangHvisJournalpostIkkeFunnet() throws HentSakSakIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
        setGsakMedSakspart();
        setJoarkThrows(HentJournalpostJournalpostIkkeFunnet.class);

        VedleggResultat vedleggResultat = tilgangskontrollService.harSaksbehandlerTilgangTilDokument("journalpostId", "fnr");
        assertFalse(vedleggResultat.harTilgang);
        assertEquals(vedleggResultat.feilmelding, JOURNALPOST_IKKE_FUNNET);
    }

    @Test
    public void harIkkeTilgangHvisJournalpostSikkerhetsbegrensning() throws HentSakSakIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
        setGsakMedSakspart();
        setJoarkThrows(HentJournalpostSikkerhetsbegrensning.class);

        VedleggResultat vedleggResultat = tilgangskontrollService.harSaksbehandlerTilgangTilDokument("journalpostId", "fnr");
        assertFalse(vedleggResultat.harTilgang);
        assertEquals(vedleggResultat.feilmelding, SIKKERHETSBEGRENSNING);
    }

    @Test
    public void harIkkeTilgangHvisGsakIkkeFinnerSak() throws HentSakSakIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
        setGsakThrows(HentSakSakIkkeFunnet.class);
        setJournalfortOgIkkeFeilRegistrert();

        VedleggResultat vedleggResultat = tilgangskontrollService.harSaksbehandlerTilgangTilDokument("journalpostId", "fnr");
        assertFalse(vedleggResultat.harTilgang);
        assertEquals(vedleggResultat.feilmelding, SAK_IKKE_FUNNET);
    }

    @Test
    public void harIkkeTilgangHvisAtkorIdIkkeFunnet() throws HentSakSakIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning, HentAktoerIdForIdentPersonIkkeFunnet {
        setGsakMedSakspart();
        setJournalfortOgIkkeFeilRegistrert();
        settAktorIdTilAFeile();

        VedleggResultat vedleggResultat = tilgangskontrollService.harSaksbehandlerTilgangTilDokument("journalpostId", "fnr");
        assertFalse(vedleggResultat.harTilgang);
        assertEquals(vedleggResultat.feilmelding, AKTOER_ID_IKKE_FUNNET);
    }

    private void setIkkeJournalfortOgIkkeFeilRegistrert() throws HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
        when(joarkService.hentJournalpost(anyString())).thenReturn(createIkkeJournalfortJournalpost());
    }

    private void setJournalfortOgIkkeFeilRegistrert() throws HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
        when(joarkService.hentJournalpost(anyString())).thenReturn(createOKJournalpost());
    }

    private void setJournalfortOgFeilRegistrert() throws HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
        when(joarkService.hentJournalpost(anyString())).thenReturn(createFeilRegistrertJournalpost());
    }

    private void setJoarkThrows(Class e) throws HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
        when(joarkService.hentJournalpost(anyString())).thenThrow(e);
    }

    private void setGsakThrows(Class e) throws HentSakSakIkkeFunnet {
        when(gSakService.hentSak(anyString())).thenThrow(e);
    }

    private void setGsakMedSakspart() throws HentSakSakIkkeFunnet {
        WSAktoer aktoerKnyttetTilSaken = new WSPerson();
        aktoerKnyttetTilSaken.setIdent(BRUKERS_IDENT);
        when(gSakService.hentSak(anyString())).thenReturn(createSak("DAG", DateTime.now().minusDays(5), aktoerKnyttetTilSaken));
    }

    private void setGsakUtenSakspart() throws HentSakSakIkkeFunnet {
        WSAktoer aktoerKnyttetTilSaken = new WSPerson();
        aktoerKnyttetTilSaken.setIdent(IKKE_BRUKERS_IDENT);
        when(gSakService.hentSak(anyString())).thenReturn(createSak("DAG", DateTime.now().minusDays(5), aktoerKnyttetTilSaken));
    }

    private void settAktorIdTilAFeile() throws HentAktoerIdForIdentPersonIkkeFunnet {
        when(fodselnummerAktorService.hentAktoerIdForIdent(any(HentAktoerIdForIdentRequest.class))).thenThrow(HentAktoerIdForIdentPersonIkkeFunnet.class);
    }

    private static WSSak createSak(String tema, DateTime opprettet, WSAktoer... brukere) {
        return new WSSak()
                .withSakId("" + idGenerator.nextInt(100000000))
                .withFagsystemSakId("" + idGenerator.nextInt(100000000))
                .withFagomraade(new WSFagomraader().withValue(tema))
                .withOpprettelsetidspunkt(opprettet)
                .withGjelderBrukerListe(brukere)
                .withSakstype(new WSSakstyper().withValue(SAKSTYPE_GENERELL))
                .withFagsystem(new WSFagsystemer().withValue("FS22"));
    }

    private Journalpost createOKJournalpost() {
        //TODO sett journalført

        Journalpost journalpost = new Journalpost();
        journalpost.setJournalpostId("journalpostid");
        journalpost.setGjelderSak(createSak(false));
        return journalpost;
    }

    private Journalpost createFeilRegistrertJournalpost() {
        //TODO sett journalført

        Journalpost journalpost = new Journalpost();
        journalpost.setJournalpostId("journalpostid");
        journalpost.setGjelderSak(createSak(true));
        return journalpost;
    }

    private Journalpost createIkkeJournalfortJournalpost() {
        //TODO sett ikke journalført

        Journalpost journalpost = new Journalpost();
        journalpost.setJournalpostId("journalpostid");
        journalpost.setGjelderSak(createSak(false));
        return journalpost;
    }

    private Sak createSak(boolean feilregistret) {
        Sak sak = new Sak();
        sak.setSakId("sakId");
        sak.setErFeilregistrert(feilregistret);
        return sak;
    }
}
