package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.HentDokumentResultat;
import no.nav.tjeneste.virksomhet.journal.v1.*;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.WSJournalpost;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.WSVariantformater;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.WSHentDokumentRequest;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.WSHentJournalpostRequest;
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
    private Journal_v1PortType joarkPortType;

    public HentDokumentResultat hentDokument(String journalpostId, String dokumentId, String fnr, String sakstemakode) {
        HentDokumentResultat resultat = tilgangskontrollService.harSaksbehandlerTilgangTilDokument(journalpostId, fnr, sakstemakode);
        
        if (resultat.harTilgang) {
            return hentDokument(journalpostId, dokumentId);
        } else {
            return resultat;
        }
    }

    private HentDokumentResultat hentDokument(String journalpostId, String dokumentId) {
        WSHentDokumentRequest request = new WSHentDokumentRequest()
                .withJournalpostId(journalpostId)
                .withDokumentId(dokumentId)
                .withVariantformat(new WSVariantformater().withValue("ARKIV"));

        try {
            byte[] dokument = joarkPortType.hentDokument(request).getDokument();
            return new HentDokumentResultat(true, dokument);
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

    public WSJournalpost hentJournalpost(String journalpostId) {
        try {
            return joarkPortType.hentJournalpost(new WSHentJournalpostRequest().withJournalpostId(journalpostId)).getJournalpost();
        } catch (HentJournalpostJournalpostIkkeFunnet | HentJournalpostSikkerhetsbegrensning e) {
            throw new SystemException("Kunne ikke hente journalpost med journalpostId: " + journalpostId, e);
        }
    }
}

