package no.nav.sbl.dialogarena.saksoversikt.service.service.interfaces;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sak;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.TjenesteResultatWrapper;

import java.util.List;

public interface JournalV2Service {

    ResultatWrapper<List<DokumentMetadata>> hentTilgjengeligJournalpostListe(List<Sak> saker, String fnr);
    TjenesteResultatWrapper hentDokument(String journalpostid, String dokumentreferanse);
}