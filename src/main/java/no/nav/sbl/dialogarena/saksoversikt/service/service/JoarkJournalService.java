package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.service.interfaces.InnsynJournalV2Service;
import no.nav.sbl.dialogarena.saksoversikt.service.service.interfaces.JournalV2Service;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sak;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.TjenesteResultatWrapper;

import javax.inject.Inject;
import java.util.List;

public class JoarkJournalService {

    @Inject
    private JournalV2Service journalV2Service;

    @Inject
    private InnsynJournalV2Service innsynJournalV2Service;

    public ResultatWrapper<List<DokumentMetadata>> joarkSakhentTilgjengeligeJournalposter(List<Sak> saker, String fnr) {
        return journalV2Service.hentTilgjengeligJournalpostListe(saker, fnr);

    }

    public TjenesteResultatWrapper hentDokument(String dokumentId, String journalpostId) {
        return journalV2Service.hentDokument(journalpostId, dokumentId);
    }

    public ResultatWrapper<String> identifiserJournalpost(String fnr){
        return innsynJournalV2Service.identifiserJournalpost(fnr);
    }

}
