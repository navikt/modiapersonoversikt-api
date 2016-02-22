package no.nav.sbl.dialogarena.saksoversikt.service.service.interfaces;

import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Sak;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.TjenesteResultatWrapper;

import java.util.List;

public interface Innsyn {

    TjenesteResultatWrapper hentTilgjengeligJournalpostListe(List<Sak> saker);
    TjenesteResultatWrapper hentDokument(String journalpostid, String dokumentreferanse);
}