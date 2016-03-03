package no.nav.sbl.dialogarena.sak.service;


import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.ModiaSakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TilgangskontrollServiceTest {

    @Mock
    private EnforcementPoint pep;
    @Mock
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @InjectMocks
    private TilgangskontrollService tilgangskontrollService = new TilgangskontrollServiceImpl();

    private static Random idGenerator = new Random();
    public static final String SAKSTYPE_GENERELL = "GEN";
    public static final String BRUKERS_IDENT = "12345678901";
    public static final String IKKE_BRUKERS_IDENT = "12345678902";
    public static final String SAKSTEMAKODE = "FOR";


    @Before
    public void setup() throws HentAktoerIdForIdentPersonIkkeFunnet {
        HentAktoerIdForIdentResponse hentAktoerIdForIdentResponse = new HentAktoerIdForIdentResponse();
        hentAktoerIdForIdentResponse.setAktoerId(BRUKERS_IDENT);
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn("0219");

        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
    }

    @Test
    public void harTilgangTilAlleTema() {
        List<Sakstema> sakstemaList = lagSakstemaListe();
        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(true);

        List<ModiaSakstema> modiaSakstemaList = tilgangskontrollService.harSaksbehandlerTilgangTilSakstema(sakstemaList, "0000");

        assertThat(modiaSakstemaList.stream().allMatch(modiaSakstema -> modiaSakstema.harTilgang), is(true));
    }


    @Test
    public void harIkkeTilgangTilNoenTema() {
        List<Sakstema> sakstemaList = lagSakstemaListe();
        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(false);

        List<ModiaSakstema> modiaSakstemaList = tilgangskontrollService.harSaksbehandlerTilgangTilSakstema(sakstemaList, "0000");

        assertThat(modiaSakstemaList.stream().allMatch(modiaSakstema -> !modiaSakstema.harTilgang), is(true));
    }

    private List<Sakstema> lagSakstemaListe() {
        return Arrays.asList(
                new Sakstema().withTemakode("PEN"),
                new Sakstema().withTemakode("TEST")
        );
    }

//    @Test
//    public void harTilgangHvisAlleSjekkerOk() throws HentSakSakIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
//        setGsakMedSakspart();
//        setJournalfortOgIkkeFeilRegistrert();
//
//        HentDokumentResultat hentDokumentResultat = tilgangskontrollService.harSaksbehandlerTilgangTilDokument("journalpostId", BRUKERS_IDENT, SAKSTEMAKODE);
//        assertTrue(hentDokumentResultat.harTilgang);
//        assertNull(hentDokumentResultat.feilmelding);
//    }
//
//    @Test
//    public void harIkkeTilgangHvisIkkeJournalfort() throws HentSakSakIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
//        setGsakMedSakspart();
//        setIkkeJournalfortOgIkkeFeilRegistrert();
//
//        HentDokumentResultat hentDokumentResultat = tilgangskontrollService.harSaksbehandlerTilgangTilDokument("journalpostId", BRUKERS_IDENT, SAKSTEMAKODE);
//        assertFalse(hentDokumentResultat.harTilgang);
//        assertEquals(IKKE_JOURNALFORT, hentDokumentResultat.feilmelding);
//    }
//
//    @Test
//    public void harIkkeTilgangHvisJournalpostHarStatusUtgaar() throws HentSakSakIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
//        setGsakMedSakspart();
//        setStatusUtgaarOgIkkeFeilRegistrert();
//
//        HentDokumentResultat hentDokumentResultat = tilgangskontrollService.harSaksbehandlerTilgangTilDokument("journalpostId", BRUKERS_IDENT, SAKSTEMAKODE);
//        assertFalse(hentDokumentResultat.harTilgang);
//        assertEquals(STATUS_UTGAAR, hentDokumentResultat.feilmelding);
//    }
//
//    @Test
//    public void harIkkeTilgangHvisJournalpostHarStatusUkjentBruker() throws HentSakSakIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
//        setGsakMedSakspart();
//        setStatusUkjentBrukerOgIkkeFeilRegistrert();
//
//        HentDokumentResultat hentDokumentResultat = tilgangskontrollService.harSaksbehandlerTilgangTilDokument("journalpostId", BRUKERS_IDENT, SAKSTEMAKODE);
//        assertFalse(hentDokumentResultat.harTilgang);
//        assertEquals(UKJENT_BRUKER, hentDokumentResultat.feilmelding);
//    }
//
//    @Test
//    public void harIkkeTilgangHvisIkkeSakspart() throws HentSakSakIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
//        setGsakUtenSakspart();
//        setJournalfortOgIkkeFeilRegistrert();
//
//        HentDokumentResultat hentDokumentResultat = tilgangskontrollService.harSaksbehandlerTilgangTilDokument("journalpostId", BRUKERS_IDENT, SAKSTEMAKODE);
//        assertFalse(hentDokumentResultat.harTilgang);
//        assertEquals(IKKE_SAKSPART, hentDokumentResultat.feilmelding);
//        assertNotNull(hentDokumentResultat.argumenterTilFeilmelding);
//    }
//
//    @Test
//    public void harIkkeTilgangHvisFeilRegistrert() throws HentSakSakIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
//        setGsakMedSakspart();
//        setJournalfortOgFeilRegistrert();
//
//        HentDokumentResultat hentDokumentResultat = tilgangskontrollService.harSaksbehandlerTilgangTilDokument("journalpostId", BRUKERS_IDENT, SAKSTEMAKODE);
//        assertFalse(hentDokumentResultat.harTilgang);
//        assertEquals(FEILREGISTRERT, hentDokumentResultat.feilmelding);
//    }
//
//    @Test(expected = SystemException.class)
//    public void harIkkeTilgangHvisJournalpostIkkeFunnet() throws HentSakSakIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
//        setGsakMedSakspart();
//        setJoarkThrows(SystemException.class);
//
//        tilgangskontrollService.harSaksbehandlerTilgangTilDokument("journalpostId", BRUKERS_IDENT, SAKSTEMAKODE);
//    }
//
//    @Test(expected = SystemException.class)
//    public void harIkkeTilgangHvisJournalpostSikkerhetsbegrensning() throws HentSakSakIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
//        setGsakMedSakspart();
//        setJoarkThrows(SystemException.class);
//
//        tilgangskontrollService.harSaksbehandlerTilgangTilDokument("journalpostId", BRUKERS_IDENT, SAKSTEMAKODE);
//    }
//
//    @Test(expected = SystemException.class)
//    public void harIkkeTilgangHvisGsakIkkeFinnerSak() throws HentSakSakIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
//        setGsakThrows(SystemException.class);
//        setJournalfortOgIkkeFeilRegistrert();
//
//        tilgangskontrollService.harSaksbehandlerTilgangTilDokument("journalpostId", BRUKERS_IDENT, SAKSTEMAKODE);
//    }
//
//    @Test
//    public void harIkkeTilgangOmEnhetIkkeHarTilgangTilTema() {
//        setGsakMedSakspart();
////        setJournalfortOgIkkeFeilRegistrert();
//        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(false);
//
//        HentDokumentResultat hentDokumentResultat = tilgangskontrollService.harSaksbehandlerTilgangTilDokument("journalpostId", BRUKERS_IDENT, SAKSTEMAKODE);
//        assertFalse(hentDokumentResultat.harTilgang);
//        assertEquals(INGEN_TILGANG, hentDokumentResultat.feilmelding);
//    }
//
////    private void setIkkeJournalfortOgIkkeFeilRegistrert() {
////        when(joarkService.hentJournalpost(anyString())).thenReturn(createIkkeJournalfortJournalpost());
////    }
////
////    private void setStatusUtgaarOgIkkeFeilRegistrert() {
////        when(joarkService.hentJournalpost(anyString())).thenReturn(createStatusUtgaarJournalpost());
////    }
////
////    private void setStatusUkjentBrukerOgIkkeFeilRegistrert() {
////        when(joarkService.hentJournalpost(anyString())).thenReturn(createUkjentBrukerJournalpost());
////    }
////
////    private void setJournalfortOgIkkeFeilRegistrert() {
////        when(joarkService.hentJournalpost(anyString())).thenReturn(createOKJournalpost());
////    }
////
////    private void setJournalfortOgFeilRegistrert() {
////        when(joarkService.hentJournalpost(anyString())).thenReturn(createFeilRegistrertJournalpost());
////    }
//
////    private void setJoarkThrows(Class e) {
////        when(joarkService.hentJournalpost(anyString())).thenThrow(e);
////    }
//
//    private void setGsakThrows(Class e) {
//        when(gSakService.hentSak(anyString())).thenThrow(e);
//    }
//
//    private void setGsakMedSakspart() {
//        WSAktoer aktoerKnyttetTilSaken = new WSPerson();
//        aktoerKnyttetTilSaken.setIdent(BRUKERS_IDENT);
//        when(gSakService.hentSak(anyString())).thenReturn(createSak("DAG", DateTime.now().minusDays(5), aktoerKnyttetTilSaken));
//    }
//
//    private void setGsakUtenSakspart() {
//        WSAktoer aktoerKnyttetTilSaken = new WSPerson();
//        aktoerKnyttetTilSaken.setIdent(IKKE_BRUKERS_IDENT);
//        when(gSakService.hentSak(anyString())).thenReturn(createSak("DAG", DateTime.now().minusDays(5), aktoerKnyttetTilSaken));
//    }
//
//    private static WSSak createSak(String tema, DateTime opprettet, WSAktoer... brukere) {
//        return new WSSak()
//                .withSakId("" + idGenerator.nextInt(100000000))
//                .withFagsystemSakId("" + idGenerator.nextInt(100000000))
//                .withFagomraade(new WSFagomraader().withValue(tema))
//                .withOpprettelsetidspunkt(opprettet)
//                .withGjelderBrukerListe(brukere)
//                .withSakstype(new WSSakstyper().withValue(SAKSTYPE_GENERELL))
//                .withFagsystem(new WSFagsystemer().withValue("FS22"));
//    }
//
//    private WSJournalpost createOKJournalpost() {
//        return new WSJournalpost()
//                .withJournalpostId("journalpostid")
//                .withJournalstatus(createJournalstatus("J"))
//                .withArkivtema(new WSArkivtemaer().withValue("arkivtema"))
//                .withGjelderSak(createSak(false));
//    }
//
//    private WSJournalpost createFeilRegistrertJournalpost() {
//        return new WSJournalpost()
//                .withJournalpostId("journalpostid")
//                .withJournalstatus(createJournalstatus("J"))
//                .withArkivtema(new WSArkivtemaer().withValue("arkivtema"))
//                .withGjelderSak(createSak(true));
//    }
//
//    private WSJournalpost createIkkeJournalfortJournalpost() {
//        return new WSJournalpost()
//                .withJournalpostId("journalpostid")
//                .withJournalstatus(createJournalstatus("N"))
//                .withArkivtema(new WSArkivtemaer().withValue("arkivtema"))
//                .withGjelderSak(createSak(false));
//    }
//
//    private WSJournalpost createStatusUtgaarJournalpost() {
//        return new WSJournalpost()
//                .withJournalpostId("journalpostid")
//                .withJournalstatus(createJournalstatus("U"))
//                .withArkivtema(new WSArkivtemaer().withValue("arkivtema"))
//                .withGjelderSak(createSak(false));
//    }
//
//    private WSJournalpost createUkjentBrukerJournalpost() {
//        return new WSJournalpost()
//                .withJournalpostId("journalpostid")
//                .withJournalstatus(createJournalstatus("UB"))
//                .withArkivtema(new WSArkivtemaer().withValue("arkivtema"))
//                .withGjelderSak(createSak(false));
//    }
//
//    private WSJournalstatuser createJournalstatus(String verdi) {
//        return new WSJournalstatuser()
//                .withValue(verdi);
//    }
//
//    private no.nav.tjeneste.virksomhet.journal.v1.informasjon.WSSak createSak(boolean feilregistret) {
//        return new no.nav.tjeneste.virksomhet.journal.v1.informasjon.WSSak()
//                .withSakId("sakId")
//                .withErFeilregistrert(feilregistret);
//    }
}
