package no.nav.sbl.dialogarena.sak.service;


import no.nav.sbl.dialogarena.sak.viewdomain.lamell.VedleggResultat;
import no.nav.tjeneste.virksomhet.journal.v1.binding.HentJournalpostJournalpostIkkeFunnet;
import no.nav.tjeneste.virksomhet.journal.v1.binding.HentJournalpostSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.Journalpost;

public interface JoarkService {

    VedleggResultat hentDokument(String journalpostId, String dokumentId, String fnr);
    Journalpost hentJournalpost(String journalpostId) throws HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning;

}
