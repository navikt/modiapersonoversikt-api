package no.nav.sbl.dialogarena.sak.service;

import no.nav.tjeneste.virksomhet.innsynjournal.v1.informasjon.Journalpost;

import java.util.stream.Stream;

public interface SaksService {
    java.util.Optional<Stream<Journalpost>> hentJournalpostListe(String fnr);

}
