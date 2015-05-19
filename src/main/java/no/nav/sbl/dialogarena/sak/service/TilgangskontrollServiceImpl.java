package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.VedleggResultat;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.Journalposimport no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSAktoer;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak;
import org.apache.commons.collections15.Predicate;
import org.slf4j.Logger;

import javax.inject.Inject;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.VedleggResultat.Feilmelding.*;
import static org.slf4j.LoggerFactory.getLogger;

public class TilgangskontrollServiceImpl implements TilgangskontrollService {

    @Inject
    private GSakService gSakService;

    @Inject
    private AktoerPortType fodselnummerAktorService;

    @Inject
    private JoarkService joarkService;

    private static final Logger logger = getLogger(TilgangskontrollService.class);

    public VedleggResultat harSaksbehandlerTilgangTilDokument(String journalpostId, String fnr) {
        return sjekkTilgang(journalpostId, fnr);
    }

    private VedleggResultat sjekkTilgang(String journalpostId, String fnr) {
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

    private boolean harJournalpostId(String journalpostid) {
        return journalpostid != null;
    }

    private Journalpost hentJournalpost(String journalpostId) {
        return joarkService.hentJournalpost(journalpostId);
    }

    private boolean erJournalfort(Journalpost journalPost) {
//        boolean erJournalfort = journalPost.getJournalstatus().equalsIgnoreCase("J");
        boolean erJournalfort = true; //Dette mangler også enn så lenge.
        if (!erJournalfort) {
            logger.warn("Journalposten med id '{}' er ikke journalført.", journalPost.getJournalpostId());
        }
        return erJournalfort;
    }

    private boolean erFeilregistrert(Journalpost journalPost) {
        boolean feilregistrert = journalPost.getGjelderSak().getErFeilregistrert();

        if (feilregistrert) {
            logger.warn("Journalposten med id '{}' er feilregistrert.", journalPost.getJournalpostId());
        }

        return feilregistrert;
    }

    private boolean erInnsenderSakspart(String sakId, String fnr) {
        WSSak gSak = hentSak(sakId);
        String aktoerId = hentAktoerIdForIdent(fnr);
        boolean erInnsenderSakspart = on(gSak.getGjelderBrukerListe()).exists(aktoerMedAktoerId(aktoerId));

        if (!erInnsenderSakspart) {
            logger.warn("Innsender med aktoerId '{}' er ikke sakspart for sak med id '{}'.", aktoerId, sakId);
        }

        return erInnsenderSakspart;
    }

    private WSSak hentSak(String sakId) {
        return gSakService.hentSak(sakId);
    }

    private String hentAktoerIdForIdent(String fnr) {
        try {
            return fodselnummerAktorService.hentAktoerIdForIdent(new HentAktoerIdForIdentRequest(fnr)).getAktoerId();
        } catch (HentAktoerIdForIdentPersonIkkeFunnet e) {
            throw new SystemException("Fant ikke aktørId for fnr: " + fnr, e);
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
