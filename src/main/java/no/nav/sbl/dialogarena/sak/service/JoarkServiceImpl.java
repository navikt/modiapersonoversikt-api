package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.HentDokumentResultat;
import no.nav.tjeneste.virksomhet.journal.v1.binding.*;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.Journalpost;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.Variantformater;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.HentDokumentRequest;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.HentJournalpostRequest;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;

import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.HentDokumentResultat.Feilmelding.*;
import static org.slf4j.LoggerFactory.getLogger;

public class JoarkServiceImpl implements JoarkService {

    private final Logger logger = getLogger(JoarkServiceImpl.class);

    @Inject
    private TilgangskontrollService tilgangskontrollService;

    @Inject
    @Named("joarkPortType")
    private JournalV1 joarkPortType;

    public HentDokumentResultat hentDokument(String journalpostId, String dokumentId, String fnr) {
        HentDokumentResultat resultat = tilgangskontrollService.harSaksbehandlerTilgangTilDokument(journalpostId, fnr);

        if (resultat.harTilgang) {
            return hentDokument(journalpostId, dokumentId);
        } else {
            return resultat;
        }
    }

    private HentDokumentResultat hentDokument(String journalpostId, String dokumentId) {
        HentDokumentRequest hentDokumentRequest = new HentDokumentRequest();
        hentDokumentRequest.setJournalpostId(journalpostId);
        hentDokumentRequest.setDokumentId(dokumentId);

        Variantformater variant = new Variantformater();
        variant.setKodeverksRef("ARKIV");
        hentDokumentRequest.setVariantformat(variant);

        try {
            Byte dokument = joarkPortType.hentDokument(hentDokumentRequest).getDokument();
            return new HentDokumentResultat(true, new byte[]{dokument});
        } catch (HentDokumentDokumentIkkeFunnet e) {
            logger.warn("Dokumentet med dokumentid '{}' ble ikke funnet", dokumentId, e.getMessage());
            return new HentDokumentResultat(false, DOKUMENT_IKKE_FUNNET);
        } catch (HentDokumentSikkerhetsbegrensning e) {
            logger.warn("Dokumentet med dokumentid '{}' kan ikke vises grunnet en sikkerhetsbegrensning", dokumentId, e.getMessage());
            return new HentDokumentResultat(false, SIKKERHETSBEGRENSNING);
        } catch (HentDokumentDokumentErSlettet e) {
            logger.warn("Dokumentet med dokumentid '{}' er slettet", dokumentId, e.getMessage());
            return new HentDokumentResultat(false, DOKUMENT_SLETTET);
        }
    }

    public Journalpost hentJournalpost(String journalpostId) {
        try {
            HentJournalpostRequest hentJournalpostRequest = new HentJournalpostRequest();
            hentJournalpostRequest.setJournalpostId(journalpostId);
            return joarkPortType.hentJournalpost(hentJournalpostRequest).getJournalpost();
        } catch (HentJournalpostJournalpostIkkeFunnet | HentJournalpostSikkerhetsbegrensning e) {
            throw new SystemException("Kunne ikke hente journalpost med journalpostId: " + journalpostId, e);
        }
    }
}

