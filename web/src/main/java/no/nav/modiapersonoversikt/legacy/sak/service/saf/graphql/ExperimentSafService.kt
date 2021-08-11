package no.nav.modiapersonoversikt.legacy.sak.service.saf.graphql

import no.nav.modiapersonoversikt.infrastructure.scientist.Scientist.Config
import no.nav.modiapersonoversikt.infrastructure.scientist.Scientist.createExperiment
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Dokument
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.DokumentMetadata
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.ResultatWrapper
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper
import no.nav.modiapersonoversikt.legacy.sak.service.saf.rest.SafService

class ExperimentSafService(
    private val control: SafService,
    private val experiment: SafService
) : SafService {
    private val experimentRate = 0.05
    private val journalportExperiment =
        createExperiment<ResultatWrapper<List<DokumentMetadata>>>(Config("SAF-Journalpost", experimentRate))

    private val dokumentExperiment =
        createExperiment<TjenesteResultatWrapper>(Config("SAF-Dokument", experimentRate))

    override fun hentJournalposter(fnr: String): ResultatWrapper<List<DokumentMetadata>> {
        return journalportExperiment.run(
            { control.hentJournalposter(fnr) },
            { experiment.hentJournalposter(fnr) }
        )
    }

    override fun hentDokument(
        journalpostId: String,
        dokumentInfoId: String,
        variantFormat: Dokument.Variantformat
    ): TjenesteResultatWrapper {
        return dokumentExperiment.run(
            { control.hentDokument(journalpostId, dokumentInfoId, variantFormat) },
            { experiment.hentDokument(journalpostId, dokumentInfoId, variantFormat) }
        )
    }
}
