package no.nav.sbl.dialogarena.sak.service;


import no.nav.sbl.dialogarena.sak.viewdomain.lamell.HentDokumentResultat;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.WSJournalpost;

public interface JoarkService {

    HentDokumentResultat hentDokument(String journalpostId, String dokumentId, String fnr);
    WSJournalpost hentJournalpost(String journalpostId);

}
