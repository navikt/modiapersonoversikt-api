package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper;

import java.util.List;

public interface JournalV2Service {

    ResultatWrapper<List<DokumentMetadata>> hentTilgjengeligJournalpostListe(List<Sak> saker, String fnr);
    TjenesteResultatWrapper hentDokument(String journalpostid, String dokumentreferanse);
}