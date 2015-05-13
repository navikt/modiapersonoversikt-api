package no.nav.sbl.dialogarena.sak.service;

import no.nav.tjeneste.virksomhet.journal.v1.binding.*;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.Journalpost;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.Variantformater;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.HentDokumentURLRequest;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.HentJournalpostRequest;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class JoarkServiceImpl implements JoarkService {

    private final Logger logger = getLogger(JoarkServiceImpl.class);

    @Inject
    @Named("joarkPortType")
    private JournalV1 joarkPortType;

    public byte[] hentDokument(String journalpostId, String dokumentId) throws HentDokumentSikkerhetsbegrensning, HentDokumentDokumentIkkeFunnet, HentDokumentDokumentErSlettet {
        HentDokumentURLRequest hentDokumentRequest = new HentDokumentURLRequest();
        hentDokumentRequest.setJournalpostId(journalpostId);
        hentDokumentRequest.setDokumentId(dokumentId);
        Variantformater variant = new Variantformater();
        variant.setKodeverksRef("ARKIV");
        hentDokumentRequest.setVariantFormat(variant);

//        HentDokumentURLResponse responseFraJoark = joarkPortType.hentDokument(hentDokumentRequest);
        //TODO Dette blir bare helt gal returverdi fra til vi får riktig responsobjekt. Dytter derfor inn mock.

        return getMockPdf();
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
        //TODO midlertidig. Byttes ut med ordentlig mock-oppsett når tjenesten kommer mer på plass
        private byte[] getMockPdf () {
            try {
                return IOUtils.toByteArray(getClass().getResourceAsStream("/mock/mock.pdf"));
            } catch (IOException e) {
                throw new RuntimeException("IOException ved henting av Mock PDFen", e);
            }
        }
    }
