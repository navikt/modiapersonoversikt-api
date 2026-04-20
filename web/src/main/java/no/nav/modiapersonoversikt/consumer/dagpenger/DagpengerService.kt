package no.nav.modiapersonoversikt.consumer.dagpenger

import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.apis.InterntApi
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.DatadelingRequestDagpengerDto
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.PeriodeDagpengerDto
import java.time.LocalDate

/**
 * Not actually a vedtak, but rather a collection of periods in which payment is
 * granted. Treated as a vedtak by our frontend for more compact display. To be
 * replaced if/when the /vedtak end point is activated.
 */
data class PseudoDagpengerVedtak(
    val perioder: List<PeriodeDagpengerDto>,
) {
    @get:JsonProperty
    val eldsteFraOgMedDato: LocalDate? get() =
        perioder
            .map {
                it.fraOgMedDato
            }.sorted()
            .firstOrNull()
}

interface DagpengerService {
    fun hentVedtak(datodelingRequest: DatadelingRequestDagpengerDto): PseudoDagpengerVedtak
}

/**
 * Our implementation of vedtak from dagpenger. The periods come sorted in
 * ascending order from dp-datadeling, as far as we can tell. We'd like to
 * display them in descending order, so we simply sort them thusly in
 * hentVedtak.
 */
open class DagpengerServiceImpl(
    val client: InterntApi,
) : DagpengerService {
    override fun hentVedtak(datodelingRequest: DatadelingRequestDagpengerDto): PseudoDagpengerVedtak {
        val response = client.dagpengerDatadelingV1PerioderPost(datodelingRequest)
        // the above should throw an exception upon failure, so we can probably
        // assume we have a DatadelingResponseDagpengerDto. TODO consider
        // instead throwing some custom exception if it still somehow is null.
        return PseudoDagpengerVedtak((response?.perioder ?: listOf()).sortedByDescending { it.fraOgMedDato })
    }
}
