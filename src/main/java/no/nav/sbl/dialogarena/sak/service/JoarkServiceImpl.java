package no.nav.sbl.dialogarena.sak.service;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.VedleggResultat;
import no.nav.tjeneste.virksomhet.journal.v1.binding.*;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.Journalpost;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.Variantformater;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.HentDokumentRequest;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.HentJournalpostRequest;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;

import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.VedleggResultat.Feilmelding.*;
import static org.slf4j.LoggerFactory.getLogger;

public class JoarkServiceImpl implements JoarkService {

    private final Logger logger = getLogger(JoarkServiceImpl.class);

    @Inject
    private TilgangskontrollService tilgangskontrollService;

    @Inject
    @Named("joarkPortType")
    private JournalV1 joarkPortType;

    public VedleggResultat hentDokument(String journalpostId, String dokumentId, String fnr) {
        VedleggResultat vedleggResultat = tilgangskontrollService.harSaksbehandlerTilgangTilDokument(journalpostId, fnr);

        if (vedleggResultat.harTilgang) {
            return hentDokument(journalpostId, dokumentId);
        } else {
            return vedleggResultat;
        }
    }

    private VedleggResultat hentDokument(String journalpostId, String dokumentId) {
        HentDokumentRequest hentDokumentRequest = new HentDokumentRequest();
        hentDokumentRequest.setJournalpostId(journalpostId);
        hentDokumentRequest.setDokumentId(dokumentId);

        Variantformater variant = new Variantformater();
        variant.setKodeverksRef("ARKIV");
        hentDokumentRequest.setVariantformat(variant);

        Byte dokument;
        try {
            dokument = joarkPortType.hentDokument(hentDokumentRequest).getDokument();
        } catch (HentDokumentDokumentIkkeFunnet hentDokumentDokumentIkkeFunnet) {
            return new VedleggResultat(false, DOKUMENT_IKKE_FUNNET);
        } catch (HentDokumentSikkerhetsbegrensning hentDokumentSikkerhetsbegrensning) {
            return new VedleggResultat(false, SIKKERHETSBEGRENSNING);
        } catch (HentDokumentDokumentErSlettet hentDokumentDokumentErSlettet) {
            return new VedleggResultat(false, DOKUMENT_SLETTET);
        }

        return new VedleggResultat(true, new byte[]{dokument});
    }

    public Journalpost hentJournalpost(String journalpostId) throws HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
        HentJournalpostRequest hentJournalpostRequest = new HentJournalpostRequest();
        hentJournalpostRequest.setJournalpostId(journalpostId);
        try {
            return joarkPortType.hentJournalpost(hentJournalpostRequest).getJournalpost();
        } catch (HentJournalpostJournalpostIkkeFunnet | HentJournalpostSikkerhetsbegrensning e) {
            logger.warn("Kunne ikke hente Journalpost.", e);
            throw e;
        }
    }
}

