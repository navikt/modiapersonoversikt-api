package no.nav.sbl.dialogarena.sak.service;

import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.journal.v1.binding.HentJournalpostJournalpostIkkeFunnet;
import no.nav.tjeneste.virksomhet.journal.v1.binding.HentJournalpostSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.Journalpost;
import no.nav.tjeneste.virksomhet.sak.v1.HentSakSakIkkeFunnet;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSAktoer;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSHentSakResponse;
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

    //Tanken er at tilgang sjekkes her og evt. feil logges og kastes videre
    //basert på hvilke feil som kastes viser Kvitteringspanelet den feilmeldingen den ønsker å vise ved de forskjellige feilene.

    //TODO Får journalpostId fra den enkelte Kvitteringen
    public boolean harSaksbehandlerTilgangTilDokument(String journalpostId, String fnr) throws HentSakSakIkkeFunnet, HentAktoerIdForIdentPersonIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
        //1. Hent JournalPost fra JOARK (ikke tilgjengelig enda). Får GSAK saksnummer og metadata og journalforingsstatus.
        // Hvis Gsak saksnummer ikke finnes er dokumentet ikke ferdig journalført og saksbehandler skal ikke få tilgang.
        return true;
        //TODO kommentert ut fram til journalpost er fullstendig og dette kan gjøres skikkelig.
//        Journalpost journalpost = hentJournalpost(journalpostId);
//        if (erJournalfort(journalpost) && erIkkeFeilRegistrert(journalpost)) {
//            if (erInnsenderSakspart(journalpost.getGjelderSak().getSakId(), fnr)) {
//                return true;
//            }
//        }
//        return false;
    }

    private Journalpost hentJournalpost(String journalpostId) throws HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
        try {
            return joarkService.hentJournalpost(journalpostId);
        } catch (HentJournalpostJournalpostIkkeFunnet | HentJournalpostSikkerhetsbegrensning e) {
            throw e;
        }
    }

    private boolean erIkkeFeilRegistrert(Journalpost journalPost) {
//        boolean riktigRegistrert = !journalPost.isSaksrelasjonFeilregistrert();
        boolean riktigRegistrert = true; //Denne mangler enn så lenge.
        if (!riktigRegistrert) {
            logger.warn("Journalposten med Id: {} er feilregistrert. Den har feilregistrert satt til true i databasen.", "journalpost.getJournalpostId");
            //TODO throwe FeilRegistrertException??
        }
        return riktigRegistrert;
    }

    private boolean erJournalfort(Journalpost journalPost) {
//        boolean erJournalfort = journalPost.getJournalstatus().equalsIgnoreCase("J");
        boolean erJournalfort = true; //Dette mangler også enn så lenge.
        if (!erJournalfort) {
            logger.warn("Journalposten med Id: {} er ikke journalført enda.", "journalpost.getJournalpostId");
            //TODO throwe IkkeJounalfortException??
        }
        return erJournalfort;
    }


    private WSHentSakResponse hentSak(String sakId) throws HentSakSakIkkeFunnet {
        try {
            return gSakServiceImpl.hentSak(sakId);
        } catch (HentSakSakIkkeFunnet hentSakSakIkkeFunnet) {
            logger.warn("Fant ikke sak hos GSak. SakId: {}", sakId);
            throw new HentSakSakIkkeFunnet();
        }
    }

    private boolean erInnsenderSakspart(String sakId, String fnr) throws HentSakSakIkkeFunnet, HentAktoerIdForIdentPersonIkkeFunnet {
        WSHentSakResponse gSak = hentSak(sakId);
        String aktoerId = hentAktoerIdForIdent(fnr);
        List<WSAktoer> brukerListe = gSak.getSak().getGjelderBrukerListe();
        for (WSAktoer aktoer : brukerListe) {
            if (aktoer.getIdent().equals(aktoerId)) {
                return true;
            }
        }
        logger.warn("Fant ikke innsender med aktoerId {] i listen over gjeldene brukere.", aktoerId);
        //TODO throwe InnsenderIkkeGjeldeneBrukerException??
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
