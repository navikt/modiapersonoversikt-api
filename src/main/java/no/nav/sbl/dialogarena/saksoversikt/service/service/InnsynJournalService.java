package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.saksoversikt.service.service.interfaces.Innsyn;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Sak;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.TjenesteResultatWrapper;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.informasjon.WSJournalpost;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;

public class InnsynJournalService {

    @Inject
    private Innsyn innsyn;

    public Optional<Stream<WSJournalpost>> joarkSakhentTilgjengeligeJournalposter(List<Sak> saker) {
        TjenesteResultatWrapper result = innsyn.hentTilgjengeligJournalpostListe(saker);

        if (result.result.isPresent()) {
            return (Optional<Stream<WSJournalpost>>) result.result.get();
        } else {
            return empty();
        }
    }

    public TjenesteResultatWrapper hentDokument(String dokumentId, String journalpostId) {
        return innsyn.hentDokument(journalpostId, dokumentId);
    }
}
