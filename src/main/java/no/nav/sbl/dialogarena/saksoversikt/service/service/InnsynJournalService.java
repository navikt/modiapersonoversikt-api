package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.service.interfaces.Innsyn;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sak;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.TjenesteResultatWrapper;

import javax.inject.Inject;
import java.util.List;

public class InnsynJournalService {

    @Inject
    private Innsyn innsyn;

    public ResultatWrapper<List<DokumentMetadata>> joarkSakhentTilgjengeligeJournalposter(List<Sak> saker, String fnr) {
        return innsyn.hentTilgjengeligJournalpostListe(saker, fnr);

    }

    public TjenesteResultatWrapper hentDokument(String dokumentId, String journalpostId) {
        return innsyn.hentDokument(journalpostId, dokumentId);
    }

}
