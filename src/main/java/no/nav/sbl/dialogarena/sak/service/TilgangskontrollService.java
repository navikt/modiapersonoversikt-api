package no.nav.sbl.dialogarena.sak.service;

import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.sak.v1.HentSakSakIkkeFunnet;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSAktoer;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSHentSakResponse;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class TilgangskontrollService {

    @Inject
    private GSakService gSakService;

    @Inject
    private AktoerPortType fodselnummerAktorService;

    private Logger logger = getLogger(TilgangskontrollService.class);

    //Tanken er at tilgang sjekkes her og evt. feil logges og kastes videre
    //basert på hvilke feil som kastes viser Kvitteringspanelet den feilmeldingen den ønsker å vise ved de forskjellige feilene.

    //TODO Får journalpostId fra den enkelte Kvitteringen
    public boolean harSaksbehandlerTilgangTilDokument(String journalpostId, String fnr) throws HentSakSakIkkeFunnet, HentAktoerIdForIdentPersonIkkeFunnet {
        //1. Hent JournalPost fra JOARK (ikke tilgjengelig enda). Får GSAK saksnummer og metadata og journalforingsstatus.
        // Hvis Gsak saksnummer ikke finnes er dokumentet ikke ferdig journalført og saksbehandler skal ikke få tilgang.

        //TODO NB!! Dette er en mock. Endres til responsen vi får tilbake fra Joark når vi henter Journalpost
        Journalpost journalPost = new Journalpost("sakId", "J", false);
        if (erJournalfort(journalPost) && erIkkeFeilRegistrert(journalPost)) {
            if (erInnsenderSakspart(journalPost.getSakId(), fnr)) {
                return true;
            }
        }
        return false;
    }

    private boolean erIkkeFeilRegistrert(Journalpost journalPost) {
        boolean riktigRegistrert = !journalPost.isSaksrelasjonFeilregistrert();
        if (!riktigRegistrert) {
            logger.warn("Journalposten med Id: {} er feilregistrert. Den har feilregistrert satt til true i databasen.", "journalpost.getJournalpostId");
            //TODO throwe FeilRegistrertException??
        }
        return riktigRegistrert;
    }

    private boolean erJournalfort(Journalpost journalPost) {
        boolean erJournalfort = journalPost.getJournalstatus().equalsIgnoreCase("J");
        if (!erJournalfort) {
            logger.warn("Journalposten med Id: {} er ikke journalført enda.", "journalpost.getJournalpostId");
            //TODO throwe IkkeJounalfortException??
        }
        return erJournalfort;
    }


    private WSHentSakResponse hentSak(String sakId) throws HentSakSakIkkeFunnet {
        try {
            return gSakService.hentSak(sakId);
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

    //TODO Dette er bare et midlertidig objekt som erstatning for kallet for å hente Journalpost fra JOARK.
    private class Journalpost {
        private String sakId;
        private String journalstatus;
        private boolean saksrelasjonFeilregistrert;

        private Journalpost(String sakId, String journalstatus, boolean saksrelasjonFeilregistrert) {
            this.sakId = sakId;
            this.journalstatus = journalstatus;
            this.saksrelasjonFeilregistrert = saksrelasjonFeilregistrert;
        }

        public String getSakId() {
            return sakId;
        }

        public String getJournalstatus() {
            return journalstatus;
        }

        public boolean isSaksrelasjonFeilregistrert() {
            return saksrelasjonFeilregistrert;
        }
    }

}
