package no.nav.sbl.dialogarena.saksoversikt.service.service.interfaces;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;

public interface InnsynJournalV2Service {

    ResultatWrapper<DokumentMetadata> identifiserJournalpost(String behandlingsId);
}
