package no.nav.modiapersonoversikt.legacy.sak.service;

import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Baksystem;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.DokumentMetadata;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.modiapersonoversikt.legacy.sak.service.saf.SafService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("squid:S1166")
public class DokumentMetadataService {

    private final SafService safService;

    public DokumentMetadataService(SafService safService) {
        this.safService = safService;
    }

    public synchronized ResultatWrapper<List<DokumentMetadata>> hentDokumentMetadata(String fnr) {
        DokumentMetadataResultat resultat = new DokumentMetadataResultat();

        hentJournalposter(fnr, resultat);   // Populerer journalposter med data fra SAF

        return new ResultatWrapper<>(
                resultat.journalposter, resultat.feilendeBaksystem);
    }

    private void hentJournalposter(String fnr, DokumentMetadataResultat resultat) {
        ResultatWrapper<List<DokumentMetadata>> journalpostWrapper = safService.hentJournalposter(fnr);
        resultat.journalposter.addAll(journalpostWrapper.resultat);
        resultat.feilendeBaksystem.addAll(journalpostWrapper.feilendeSystemer);
    }
}

class DokumentMetadataResultat {
    List<DokumentMetadata> journalposter;
    List<DokumentMetadata> soknader;
    Set<Baksystem> feilendeBaksystem;

    DokumentMetadataResultat() {
        journalposter = new ArrayList<>();
        soknader = new ArrayList<>();
        feilendeBaksystem = new HashSet<>();
    }
}
