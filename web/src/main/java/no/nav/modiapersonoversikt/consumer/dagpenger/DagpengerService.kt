package no.nav.modiapersonoversikt.consumer.dagpenger

import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.apis.InterntApi
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.BeregnetDagDagpengerDto
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.DatadelingRequestDagpengerDto
import java.time.LocalDate

/**
 * BeregnetDagDagpengerDto representerer utbetalinger av dagpenger basert på meldekort som sendes hver 14. dag.
 * Meldekortet beregnes og en får utbetalt en sum per dag i den 14. dagers perioden.
 */
data class Dagpenger(
    val perioder: List<BeregnetDagDagpengerDto>,
) {
    @get:JsonProperty
    val eldsteFraOgMedDato: LocalDate? get() =
        perioder.minOfOrNull {
            it.fraOgMed
        }
}

interface DagpengerService {
    fun hentDagpenger(datodelingRequest: DatadelingRequestDagpengerDto): Dagpenger
}

/**
 * The periods come sorted in
 * ascending order from dp-datadeling, as far as we can tell. We'd like to
 * display them in descending order.
 */
open class DagpengerServiceImpl(
    val client: InterntApi,
) : DagpengerService {
    override fun hentDagpenger(datodelingRequest: DatadelingRequestDagpengerDto): Dagpenger {
        val response = client.dagpengerDatadelingV1BeregningerPost(datodelingRequest)
        // the above should throw an exception upon failure, so we can probably
        // assume we have a DatadelingResponseDagpengerDto. TODO consider
        // instead throwing some custom exception if it still somehow is null.
        return Dagpenger((response ?: listOf()).sortedByDescending { it.fraOgMed })
    }
}
