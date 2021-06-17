package no.nav.modiapersonoversikt.legacy.sak.service.interfaces;

import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper;

public interface JournalV2Service {

    TjenesteResultatWrapper hentDokument(String journalpostid, String dokumentreferanse);
}