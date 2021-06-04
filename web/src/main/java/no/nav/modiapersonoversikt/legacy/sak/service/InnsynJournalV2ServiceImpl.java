package no.nav.modiapersonoversikt.legacy.sak.service;

import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Baksystem;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Dokument;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.DokumentMetadata;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.FeilendeBaksystemException;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.modiapersonoversikt.legacy.sak.service.interfaces.InnsynJournalV2Service;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.binding.*;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.informasjon.InnsynDokument;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.meldinger.IdentifiserJournalpostRequest;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.meldinger.IdentifiserJournalpostResponse;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static no.nav.modiapersonoversikt.legacy.sak.providerdomain.Baksystem.JOARK;
import static org.slf4j.LoggerFactory.getLogger;

public class InnsynJournalV2ServiceImpl implements InnsynJournalV2Service {


    private Logger logger = getLogger(InnsynJournalV2ServiceImpl.class);

    @Autowired
    private InnsynJournalV2 innsynJournalV2;


    public InnsynJournalV2ServiceImpl(InnsynJournalV2 innsynJournalV2){
        this.innsynJournalV2 = innsynJournalV2;
    }

    @Override
    public ResultatWrapper<DokumentMetadata> identifiserJournalpost(String behandlingsId) {

        try {
            IdentifiserJournalpostResponse response = innsynJournalV2.identifiserJournalpost(lagRequest(behandlingsId));

            return new ResultatWrapper<>(responseTilDokumentMetadata(response));

        } catch (IdentifiserJournalpostJournalpostIkkeInngaaende e) {
            logger.warn("Journalpost med behandlingsid '{}' er ikke inngående. '{}'", behandlingsId, e.getMessage());
            return joarkFeilendeBaksystem();
        } catch (IdentifiserJournalpostUgyldigAntallJournalposter e) {
            logger.warn("Uthenting av journalpost '{}' resulterte ikke i nøyaktig én journalpost. '{}' ", behandlingsId, e.getMessage());
            return joarkFeilendeBaksystem();
        } catch (IdentifiserJournalpostObjektIkkeFunnet e) {
            logger.warn("Journalposten for behandlingsid '{}' ble ikke funnet. '{}' ", behandlingsId, e.getMessage());
            return joarkFeilendeBaksystem();
        } catch (IdentifiserJournalpostUgyldingInput e) {
            logger.error("Påkrevd inputparameter er ikke satt. Behandlingsid: '{}' ", behandlingsId, e.getMessage());
            return joarkFeilendeBaksystem();
        } catch (RuntimeException e) {
            logger.error("Det skjedde en ukjent feil under henting av journalpost med behandlingsId '{}'. '{}' ", behandlingsId, e);
            throw new FeilendeBaksystemException(JOARK);
        }
    }

    private IdentifiserJournalpostRequest lagRequest(String behandlingsId) {
        return new IdentifiserJournalpostRequest()
                    .withKanalReferanseId(behandlingsId);
    }

    private DokumentMetadata responseTilDokumentMetadata(IdentifiserJournalpostResponse response) {
        return new DokumentMetadata()
                        .withHoveddokument(new Dokument().withDokumentreferanse(response.getHoveddokument().getDokumentId())
                                .withKanVises(InnsynDokument.JA.equals(response.getHoveddokument().getInnsynDokument())))
                        .withJournalpostId(response.getJournalpostId())
                        .withVedlegg(joarkVedleggTilVedlegg(response.getVedleggListe()));
    }

    private List<Dokument> joarkVedleggTilVedlegg(List<no.nav.tjeneste.virksomhet.innsynjournal.v2.informasjon.Dokument> joarkDokumenter){
        return joarkDokumenter
                .stream()
                .map(dokument ->
                        new Dokument()
                                .withTittel(dokument.getTittel())
                                .withKanVises(dokument.getInnsynDokument().equals(InnsynDokument.JA))
                                .withLogiskDokument(false)
                                .withDokumentreferanse(dokument.getDokumentId())
                ).collect(toList());
    }

    private ResultatWrapper<DokumentMetadata> joarkFeilendeBaksystem() {
        Set<Baksystem> feilendeBaksystemer = new HashSet<>();
        feilendeBaksystemer.add(JOARK);
        return new ResultatWrapper<>(null, feilendeBaksystemer);
    }
}
