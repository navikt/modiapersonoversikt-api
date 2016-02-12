package no.nav.sbl.dialogarena.sak.service;


import no.nav.sbl.dialogarena.sak.viewdomain.detalj.Sak;
import no.nav.sbl.dialogarena.sak.viewdomain.detalj.TjenesteResultatWrapper;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.informasjon.Journalpost;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface InnsynJournalService {
    Optional<Stream<Journalpost>> joarkSakhentTilgjengeligeJournalposter(List<Sak> saker);

    TjenesteResultatWrapper hentDokument(String dokumentId, String journalpostId);
}
