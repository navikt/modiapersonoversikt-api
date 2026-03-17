package no.nav.modiapersonoversikt.consumer.dagpenger

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
    val nyesteFraOgMedDato: LocalDate? get() =
        perioder
            .map {
                it.fraOgMedDato
            }.sortedDescending()
            .firstOrNull()
}

interface DagpengerService {
    fun hentVedtak(datodelingRequest: DatadelingRequestDagpengerDto): PseudoDagpengerVedtak
}

open class DagpengerServiceImpl(
    val client: InterntApi,
) : DagpengerService {
    override fun hentVedtak(datodelingRequest: DatadelingRequestDagpengerDto): PseudoDagpengerVedtak {
        val response = client.dagpengerDatadelingV1PerioderPost(datodelingRequest)
        // the above should throw an exception upon failure, so we can safely
        // assume we have a DatadelingResponseDagpengerDto.
        return PseudoDagpengerVedtak(response!!.perioder)
    }
}
