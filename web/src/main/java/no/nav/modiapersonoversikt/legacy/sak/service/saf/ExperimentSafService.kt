package no.nav.modiapersonoversikt.legacy.sak.service.saf

import com.expediagroup.graphql.types.GraphQLResponse
import no.nav.modiapersonoversikt.infrastructure.scientist.Scientist
import no.nav.modiapersonoversikt.infrastructure.scientist.Scientist.Config
import no.nav.modiapersonoversikt.infrastructure.scientist.Scientist.createExperiment
import no.nav.modiapersonoversikt.legacy.api.domain.saf.generated.Hentbrukerssaker
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Dokument
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.DokumentMetadata
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.ResultatWrapper
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService

class ExperimentSafService(
    private val control: SafService,
    private val experiment: SafService,
    unleashService: UnleashService
) : SafService {
    private val experimentRate = Scientist.UnleashRate(unleashService, Feature.SAF_RATE)
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

    override fun hentSaker(ident: String): GraphQLResponse<Hentbrukerssaker.Result> {
        return experiment.hentSaker(ident)
    }
}
