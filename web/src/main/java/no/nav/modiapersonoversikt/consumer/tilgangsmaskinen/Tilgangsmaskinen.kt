package no.nav.modiapersonoversikt.consumer.tilgangsmaskinen

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.benmanes.caffeine.cache.Cache
import no.nav.common.health.HealthCheckUtils
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.types.identer.Fnr
import no.nav.common.types.identer.NavIdent
import no.nav.common.utils.UrlUtils
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.generated.apis.TilgangControllerApi
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.generated.infrastructure.ApiResponse
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.generated.infrastructure.ClientError
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.generated.infrastructure.ResponseType
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.generated.models.ProblemDetailResponse
import no.nav.modiapersonoversikt.infrastructure.cache.CacheUtils
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import no.nav.personoversikt.common.logging.TjenestekallLogger
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory

interface Tilgangsmaskinen : Pingable {
    fun sjekkTilgang(
        veilederIdent: NavIdent,
        fnr: Fnr,
    ): TilgangsMaskinResponse?
}

data class TilgangsMaskinResponse(
    val harTilgang: Boolean,
    val error: ProblemDetailResponse? = null,
)

open class TilgangsmaskinenImpl(
    private val url: String,
    private val client: OkHttpClient,
    private val tjenestekallLogger: TjenestekallLogger,
    private val objectMapper: ObjectMapper,
    private val cache: Cache<String, TilgangsMaskinResponse?> = CacheUtils.createCache(),
) : Tilgangsmaskinen {
    private val tilgangsMaskinenApi = TilgangControllerApi(url, client)
    private val logger = LoggerFactory.getLogger(TilgangsmaskinenImpl::class.java)

    override fun sjekkTilgang(
        veilederIdent: NavIdent,
        fnr: Fnr,
    ): TilgangsMaskinResponse? =
        cache.get("${veilederIdent.get()}-${fnr.get()}") {
            runCatching {
                val response = tilgangsMaskinenApi.kompletteReglerCCFWithHttpInfo(veilederIdent.get(), fnr.get())
                when (response.responseType) {
                    ResponseType.Success -> TilgangsMaskinResponse(harTilgang = true)
                    ResponseType.ClientError -> makeErrorResponse(response)
                    else -> {
                        logger.error("Received unexpected response type: ${response.responseType}")
                        TilgangsMaskinResponse(harTilgang = false)
                    }
                }
            }.getOrElse { err ->
                tjenestekallLogger.error(
                    header = "Greide ikke å hente tilgang fra tilgangsmaskinen",
                    fields = mapOf("fnr" to fnr.get()),
                    throwable = err,
                )
                null
            }
        }

    private fun makeErrorResponse(response: ApiResponse<*>): TilgangsMaskinResponse {
        if (response.statusCode == 403) {
            response as ClientError<*>
            try {
                val errorObject = objectMapper.readValue(response.body as String, ProblemDetailResponse::class.java)
                return TilgangsMaskinResponse(harTilgang = false, error = errorObject)
            } catch (e: Exception) {
                logger.warn("Parse exception when parsing error response from tilgangsmaskinen: ${e.message}")
                return TilgangsMaskinResponse(harTilgang = false)
            }
        }
        logger.warn("Received error response from tilgangsmaskinen: ${response.statusCode}")
        return TilgangsMaskinResponse(harTilgang = false)
    }

    override fun ping() =
        SelfTestCheck(
            "pdl-pip-api via $url",
            false,
        ) {
            HealthCheckUtils.pingUrl(UrlUtils.joinPaths(url, "/internal/health/liveness"), client)
        }
}
