package no.nav.modiapersonoversikt.consumer.arbeidssoekerregisteret

import no.nav.modiapersonoversikt.consumer.arbeidssoekerregisteret.generated.apis.DefaultApi
import no.nav.modiapersonoversikt.consumer.arbeidssoekerregisteret.generated.models.AggregertPeriodeArbeidssoekerregisteretDto
import no.nav.modiapersonoversikt.consumer.arbeidssoekerregisteret.generated.models.QueryRequestArbeidssoekerregisteretDto
import java.util.UUID

typealias OppslagArbeidssoekerregisteret = AggregertPeriodeArbeidssoekerregisteretDto

interface ArbeidssoekerregisteretService {
    fun hentOppslag(fnr: String): OppslagArbeidssoekerregisteret?
}

private data class IdentitetsnummerRequest(
    override val identitetsnummer: String,
    override val type: QueryRequestArbeidssoekerregisteretDto.Type = QueryRequestArbeidssoekerregisteretDto.Type.IDENTITETSNUMMER,
    override val perioder: List<UUID> = emptyList(),
) : QueryRequestArbeidssoekerregisteretDto

open class ArbeidssoekerregisteretServiceImpl(
    private val api: DefaultApi,
) : ArbeidssoekerregisteretService {
    override fun hentOppslag(fnr: String): OppslagArbeidssoekerregisteret? = api.apiV3SnapshotPost(IdentitetsnummerRequest(fnr))
}
