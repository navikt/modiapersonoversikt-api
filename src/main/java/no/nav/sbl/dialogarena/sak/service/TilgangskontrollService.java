package no.nav.sbl.dialogarena.sak.service;

import no.nav.sbl.dialogarena.sak.tilgang.TilgangFeilmeldinger;
import no.nav.sbl.dialogarena.sak.tilgang.TilgangsKontrollResult;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.journal.v1.binding.HentJournalpostJournalpostIkkeFunnet;
import no.nav.tjeneste.virksomhet.journal.v1.binding.HentJournalpostSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.Journalpost;
import no.nav.tjeneste.virksomhet.sak.v1.HentSakSakIkkeFunnet;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSAktoer;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class TilgangskontrollService {

    @Inject
    private GSakService gSakServiceImpl;

    @Inject
    private AktoerPortType fodselnummerAktorService;

    @Inject
    private JoarkService joarkService;

    private Logger logger = getLogger(TilgangskontrollService.class);

    public TilgangsKontrollResult harSaksbehandlerTilgangTilDokument(String journalpostId, String fnr) {
        TilgangsKontrollResult resultat;
        try {
            resultat = sjekkTilgang(journalpostId, fnr);
        } catch (HentAktoerIdForIdentPersonIkkeFunnet e) {
            return new TilgangsKontrollResult(false, TilgangFeilmeldinger.AKTOER_ID_IKKE_FUNNET);
        } catch (HentSakSakIkkeFunnet e) {
            return new TilgangsKontrollResult(false, TilgangFeilmeldinger.SAK_IKKE_FUNNET);
        } catch (HentJournalpostJournalpostIkkeFunnet e) {
            return new TilgangsKontrollResult(false, TilgangFeilmeldinger.JOURNALPOST_IKKE_FUNNET);
        } catch (HentJournalpostSikkerhetsbegrensning e) {
            return new TilgangsKontrollResult(false, TilgangFeilmeldinger.SIKKERHETSBEGRENSNING);
        } catch (Exception e) {
            return new TilgangsKontrollResult(false, TilgangFeilmeldinger.UKJENT_FEIL);
        }
        return resultat;
    }

    private TilgangsKontrollResult sjekkTilgang(String journalpostId, String fnr) throws HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning, HentSakSakIkkeFunnet, HentAktoerIdForIdentPersonIkkeFunnet {
        Journalpost journalpost = hentJournalpost(journalpostId);
        if (!erJournalfort(journalpost)) {
            return new TilgangsKontrollResult(false, TilgangFeilmeldinger.IKKE_JOURNALFORT);
        } else if (erFeilregistrert(journalpost)) {
            return new TilgangsKontrollResult(false, TilgangFeilmeldinger.FEILREGISTRERT);
        } else if (!erInnsenderSakspart(journalpost.getGjelderSak().getSakId(), fnr)) {
            return new TilgangsKontrollResult(false, TilgangFeilmeldinger.IKKE_SAKSPART);
        }
        return new TilgangsKontrollResult(true);
    }

    private Journalpost hentJournalpost(String journalpostId) throws HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
        try {
            return joarkService.hentJournalpost(journalpostId);
        } catch (HentJournalpostJournalpostIkkeFunnet | HentJournalpostSikkerhetsbegrensning e) {
            logger.warn("Exception i hentJournalpost. ", e);
            throw e;
        }
    }

    private boolean erJournalfort(Journalpost journalPost) {
//        boolean erJournalfort = journalPost.getJournalstatus().equalsIgnoreCase("J");
        boolean erJournalfort = true; //Dette mangler også enn så lenge.
        if (!erJournalfort) {
            logger.warn("Journalposten med Id: {} er ikke journalført enda.", "journalpost.getJournalpostId");
            return false;
        }
        return true;
    }

    private boolean erFeilregistrert(Journalpost journalPost) {
        boolean feilregistrert = journalPost.getGjelderSak().getErFeilregistrert();
        if (feilregistrert) {
            logger.warn("Journalposten med Id: {} er feilregistrert. Den har feilregistrert satt til true i databasen.", journalPost.getJournalpostId());
            return true;
        }
        return false;
    }

    private WSSak hentSak(String sakId) throws HentSakSakIkkeFunnet {
        try {
            return gSakServiceImpl.hentSak(sakId);
        } catch (HentSakSakIkkeFunnet hentSakSakIkkeFunnet) {
            logger.warn("Fant ikke sak hos GSak. SakId: {}", sakId);
            throw new HentSakSakIkkeFunnet();
        }
    }

    private boolean erInnsenderSakspart(String sakId, String fnr) throws HentSakSakIkkeFunnet, HentAktoerIdForIdentPersonIkkeFunnet {
        WSSak gSak = hentSak(sakId);
        String aktoerId = hentAktoerIdForIdent(fnr);
        List<WSAktoer> brukerListe = gSak.getGjelderBrukerListe();
        for (WSAktoer aktoer : brukerListe) {
            if (aktoer.getIdent().equals(aktoerId)) {
                return true;
            }
        }
        logger.warn("Fant ikke innsender med aktoerId {] i listen over gjeldene brukere.", aktoerId);
        return false;
    }

    private String hentAktoerIdForIdent(String fnr) throws HentAktoerIdForIdentPersonIkkeFunnet {
        try {
            return fodselnummerAktorService.hentAktoerIdForIdent(new HentAktoerIdForIdentRequest(fnr)).getAktoerId();
        } catch (HentAktoerIdForIdentPersonIkkeFunnet hentAktoerIdForIdentPersonIkkeFunnet) {
            logger.warn("Fant ikke AktoerId for Fnr: {} ", fnr);
            throw new HentAktoerIdForIdentPersonIkkeFunnet();
        }
    }
}
