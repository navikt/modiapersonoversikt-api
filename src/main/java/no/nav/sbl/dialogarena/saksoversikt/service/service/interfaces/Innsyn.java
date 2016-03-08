package no.nav.sbl.dialogarena.saksoversikt.service.service.interfaces;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sak;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.TjenesteResultatWrapper;

import java.util.List;

public interface Innsyn {

    TjenesteResultatWrapper hentTilgjengeligJournalpostListe(List<Sak> saker, String fnr);
    TjenesteResultatWrapper hentDokument(String journalpostid, String dokumentreferanse);
}