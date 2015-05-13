package no.nav.sbl.dialogarena.sak.service;


import no.nav.tjeneste.virksomhet.journal.v1.binding.*;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.Journalpost;

public interface JoarkService {

    byte[] hentDokument(String journalpostId, String dokumentId) throws HentDokumentSikkerhetsbegrensning, HentDokumentDokumentIkkeFunnet, HentDokumentDokumentErSlettet;
    Journalpost hentJournalpost(String journalpostId) throws HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning;

}
