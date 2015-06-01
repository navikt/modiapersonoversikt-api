package no.nav.sbl.dialogarena.sak.service;


import no.nav.sbl.dialogarena.sak.viewdomain.lamell.HentDokumentResultat;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.Journalpost;

public interface JoarkService {

    HentDokumentResultat hentDokument(String journalpostId, String dokumentId, String fnr);
    Journalpost hentJournalpost(String journalpostId);

}
