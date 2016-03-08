package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.service.interfaces.Innsyn;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sak;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.TjenesteResultatWrapper;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;

public class InnsynJournalService {

    @Inject
    private Innsyn innsyn;

    public Optional<Stream<DokumentMetadata>> joarkSakhentTilgjengeligeJournalposter(List<Sak> saker, String fnr) {
        TjenesteResultatWrapper result = innsyn.hentTilgjengeligJournalpostListe(saker, fnr);

        if (result.result.isPresent()) {
            return Optional.of((Stream<DokumentMetadata>) result.result.get());
        } else {
            return empty();
        }
    }

    public TjenesteResultatWrapper hentDokument(String dokumentId, String journalpostId) {
        return innsyn.hentDokument(journalpostId, dokumentId);
    }

}
