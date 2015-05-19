package no.nav.sbl.dialogarena.sak.service;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.VedleggResultat;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.journal.v1.binding.HentJournalpostJournalpostIkkeFunnet;
import no.nav.tjeneste.virksomhet.journal.v1.binding.HentJournalpostSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.Journalpost;
import no.nav.tjeneste.virksomhet.sak.v1.HentSakSakIkkeFunnet;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSAktoer;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak;
import org.apache.commons.collections15.Predicate;
import org.slf4j.Logger;

import javax.inject.Inject;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.VedleggResultat.Feilmelding.*;
import static org.slf4j.LoggerFactory.getLogger;

public class TilgangskontrollServiceImpl implements TilgangskontrollService {

    @Inject
    private GSakService gSakServiceImpl;

    @Inject
    private AktoerPortType fodselnummerAktorService;

    @Inject
    private JoarkService joarkService;

    private Logger logger = getLogger(TilgangskontrollService.class);

    public VedleggResultat harSaksbehandlerTilgangTilDokument(String journalpostId, String fnr) {
        VedleggResultat resultat;
        try {
            resultat = sjekkTilgang(journalpostId, fnr);
        } catch (HentAktoerIdForIdentPersonIkkeFunnet e) {
            return new VedleggResultat(false, AKTOER_ID_IKKE_FUNNET);
        } catch (HentSakSakIkkeFunnet e) {
            return new VedleggResultat(false, SAK_IKKE_FUNNET);
        } catch (HentJournalpostJournalpostIkkeFunnet e) {
            return new VedleggResultat(false, JOURNALPOST_IKKE_FUNNET);
        } catch (HentJournalpostSikkerhetsbegrensning e) {
            return new VedleggResultat(false, SIKKERHETSBEGRENSNING);
        } catch (Exception e) {
            return new VedleggResultat(false, UKJENT_FEIL);
        }
        return resultat;
    }

    private boolean harJournalpostId(String journalpostid) {
        return journalpostid != null;
    }

    private VedleggResultat sjekkTilgang(String journalpostId, String fnr) throws HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning, HentSakSakIkkeFunnet, HentAktoerIdForIdentPersonIkkeFunnet {
        if (!harJournalpostId(journalpostId)) {
            return new VedleggResultat(false, IKKE_JOURNALFORT);
        }
        
        Journalpost journalpost = hentJournalpost(journalpostId);
        if (!erJournalfort(journalpost)) {
            return new VedleggResultat(false, IKKE_JOURNALFORT);
        } else if (erFeilregistrert(journalpost)) {
            return new VedleggResultat(false, FEILREGISTRERT);
        } else if (!erInnsenderSakspart(journalpost.getGjelderSak().getSakId(), fnr)) {
            return new VedleggResultat(false, IKKE_SAKSPART);
        }
        return new VedleggResultat(true);
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
        }
        return erJournalfort;
    }

    private boolean erFeilregistrert(Journalpost journalPost) {
        boolean feilregistrert = journalPost.getGjelderSak().getErFeilregistrert();
        if (feilregistrert) {
            logger.warn("Journalposten med Id: {} er feilregistrert. Den har feilregistrert satt til true i databasen.", journalPost.getJournalpostId());
        }
        return feilregistrert;
    }

    private boolean erInnsenderSakspart(String sakId, String fnr) throws HentSakSakIkkeFunnet, HentAktoerIdForIdentPersonIkkeFunnet {
        WSSak gSak = hentSak(sakId);
        final String aktoerId = hentAktoerIdForIdent(fnr);
        boolean erInnsenderSakspart = on(gSak.getGjelderBrukerListe()).exists(aktoerMedAktoerId(aktoerId));

        if (!erInnsenderSakspart) {
            logger.warn("Fant ikke innsender med aktoerId {} i listen over gjeldene brukere.", aktoerId);
        }

        return erInnsenderSakspart;
    }

    private WSSak hentSak(String sakId) throws HentSakSakIkkeFunnet {
        try {
            return gSakServiceImpl.hentSak(sakId);
        } catch (HentSakSakIkkeFunnet hentSakSakIkkeFunnet) {
            logger.warn("Fant ikke sak hos GSak. SakId: {}", sakId);
            throw hentSakSakIkkeFunnet;
        }
    }


    private String hentAktoerIdForIdent(String fnr) throws HentAktoerIdForIdentPersonIkkeFunnet {
        try {
            return fodselnummerAktorService.hentAktoerIdForIdent(new HentAktoerIdForIdentRequest(fnr)).getAktoerId();
        } catch (HentAktoerIdForIdentPersonIkkeFunnet hentAktoerIdForIdentPersonIkkeFunnet) {
            logger.warn("Fant ikke AktoerId for Fnr: {} ", fnr);
            throw hentAktoerIdForIdentPersonIkkeFunnet;
        }
    }

    private static Predicate<WSAktoer> aktoerMedAktoerId(final String aktoerId) {
        return new Predicate<WSAktoer>() {
            @Override
            public boolean evaluate(WSAktoer wsAktoer) {
                return wsAktoer.getIdent().equals(aktoerId);
            }
        };
    }
}
