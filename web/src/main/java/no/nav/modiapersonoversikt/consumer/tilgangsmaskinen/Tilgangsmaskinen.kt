package no.nav.modiapersonoversikt.consumer.tilgangsmaskinen

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.benmanes.caffeine.cache.Cache
import io.ktor.util.reflect.instanceOf
import no.nav.common.health.HealthCheckUtils
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.types.identer.Fnr
import no.nav.common.utils.UrlUtils
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.generated.apis.TilgangControllerApi
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.generated.infrastructure.ClientError
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.generated.infrastructure.ClientException
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.generated.models.ForbiddenResponse
import no.nav.modiapersonoversikt.infrastructure.cache.CacheUtils
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import no.nav.personoversikt.common.logging.TjenestekallLogger
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus

interface Tilgangsmaskinen : Pingable {
    fun sjekkTilgang(fnr: Fnr): TilgangsMaskinResponse?
}

data class TilgangsMaskinResponse(
    val harTilgang: Boolean,
    val error: ForbiddenResponse? = null,
)

@CacheConfig(cacheNames = ["tilgangsmaskinenCache"], keyGenerator = "userkeygenerator")
open class TilgangsmaskinenImpl(
    private val url: String,
    private val client: OkHttpClient,
    private val tjenestekallLogger: TjenestekallLogger,
    private val objectMapper: ObjectMapper,
    private val cache: Cache<String, TilgangsMaskinResponse?> = CacheUtils.createCache(),
) : Tilgangsmaskinen {
    private val tilgangsMaskinenApi = TilgangControllerApi(url, client)
    private val logger = LoggerFactory.getLogger(TilgangsmaskinenImpl::class.java)

    @Cacheable
    override fun sjekkTilgang(fnr: Fnr): TilgangsMaskinResponse? =
        cache.get(fnr.get()) {
            runCatching {
                tilgangsMaskinenApi.kompletteRegler(fnr.get())
                TilgangsMaskinResponse(harTilgang = true)
            }.getOrElse { err ->
                if (err.instanceOf(ClientException::class) && (err as ClientException).statusCode == HttpStatus.FORBIDDEN.value()) {
                    val errorRes = err.response as ClientError<*>
                    try {
                        val errorObject = objectMapper.readValue(errorRes.body as String, ForbiddenResponse::class.java)
                        TilgangsMaskinResponse(harTilgang = false, error = errorObject)
                    } catch (e: Exception) {
                        logger.warn("Parse exception when parsing error response from tilgangsmaskinen: ${e.message}")
                    }
                    TilgangsMaskinResponse(harTilgang = false)
                }

                tjenestekallLogger.error(
                    header = "Greide ikke å hente tilgang fra tilgangsmaskinen",
                    fields = mapOf("fnr" to fnr.get()),
                    throwable = err,
                )
                null
            }
        }

    override fun ping() =
        SelfTestCheck(
            "pdl-pip-api via $url",
            false,
        ) {
            HealthCheckUtils.pingUrl(UrlUtils.joinPaths(url, "/internal/health/liveness"), client)
        }
}
