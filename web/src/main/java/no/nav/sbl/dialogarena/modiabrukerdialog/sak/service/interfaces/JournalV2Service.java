package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper;

public interface JournalV2Service {

    TjenesteResultatWrapper hentDokument(String journalpostid, String dokumentreferanse);
}