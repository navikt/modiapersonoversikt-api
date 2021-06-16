package no.nav.modiapersonoversikt.legacy.sak.service.interfaces;

import no.nav.modiapersonoversikt.legacy.sak.providerdomain.DokumentMetadata;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.ResultatWrapper;

public interface InnsynJournalV2Service {

    ResultatWrapper<DokumentMetadata> identifiserJournalpost(String behandlingsId);
}
