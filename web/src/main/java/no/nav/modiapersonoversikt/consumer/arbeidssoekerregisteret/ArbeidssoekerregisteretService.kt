package no.nav.modiapersonoversikt.consumer.arbeidssoekerregisteret

import com.fasterxml.jackson.annotation.JsonTypeName
import no.nav.modiapersonoversikt.consumer.arbeidssoekerregisteret.generated.apis.DefaultApi
import no.nav.modiapersonoversikt.consumer.arbeidssoekerregisteret.generated.infrastructure.ClientException
import no.nav.modiapersonoversikt.consumer.arbeidssoekerregisteret.generated.models.AggregertPeriodeArbeidssoekerregisteretDto
import no.nav.modiapersonoversikt.consumer.arbeidssoekerregisteret.generated.models.QueryRequestArbeidssoekerregisteretDto
import java.util.UUID

typealias OppslagArbeidssoekerregisteret = AggregertPeriodeArbeidssoekerregisteretDto

interface ArbeidssoekerregisteretService {
    fun hentOppslag(fnr: String): OppslagArbeidssoekerregisteret?
}

@JsonTypeName("IDENTITETSNUMMER")
private data class IdentitetsnummerRequest(
    override val identitetsnummer: String,
    override val type: QueryRequestArbeidssoekerregisteretDto.Type = QueryRequestArbeidssoekerregisteretDto.Type.IDENTITETSNUMMER,
    override val perioder: List<UUID> = emptyList(),
) : QueryRequestArbeidssoekerregisteretDto

open class ArbeidssoekerregisteretServiceImpl(
    private val api: DefaultApi,
) : ArbeidssoekerregisteretService {
    override fun hentOppslag(fnr: String): OppslagArbeidssoekerregisteret? =
        try {
            api.apiV3SnapshotPost(IdentitetsnummerRequest(fnr))
        } catch (e: ClientException) {
            // Når det ikke finnes oppslag på personen returnerer APIet 403. Vi ønsker å returnere null med status 200
            if (e.statusCode == 404) null else throw e
        }
}
