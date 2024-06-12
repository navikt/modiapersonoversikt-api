package no.nav.modiapersonoversikt.consumer.pdlFullmaktApi

import io.ktor.util.encodeBase64
import no.nav.common.health.HealthCheckUtils
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.types.identer.Fnr
import no.nav.common.utils.UrlUtils
import no.nav.modiapersonoversikt.consumer.pdlFullmaktApi.generated.apis.FullmaktInternalControllerApi
import no.nav.modiapersonoversikt.consumer.pdlFullmaktApi.generated.models.FullmakIdentRequest
import no.nav.modiapersonoversikt.consumer.pdlFullmaktApi.generated.models.FullmaktDetails
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import okhttp3.OkHttpClient
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable

interface PdlFullmaktApi : Pingable {
    fun hentFullmakterForFullmektig(fnr: Fnr): List<FullmaktDetails>?

    fun hentFullmakterForFullmaktsgiver(fnr: Fnr): List<FullmaktDetails>?
}

@CacheConfig(cacheNames = ["pdlFullmaktCache"], keyGenerator = "userkeygenerator")
open class PdlFullmaktApiImpl(
    private val url: String,
    private val client: OkHttpClient,
) : PdlFullmaktApi {
    private val pdlFullmaktApi = FullmaktInternalControllerApi(url, client)

    @Cacheable
    override fun hentFullmakterForFullmektig(fnr: Fnr): List<FullmaktDetails>? {
        return pdlFullmaktApi.getFullmaktForFullmektig(fullmaktRequest(fnr.get()))
    }

    @Cacheable
    override fun hentFullmakterForFullmaktsgiver(fnr: Fnr): List<FullmaktDetails>? {
        return pdlFullmaktApi.getFullmaktForFullmaktsgiver(fullmaktRequest(fnr.get()))
    }

    private fun fullmaktRequest(ident: String) = FullmakIdentRequest(ident = ident.encodeBase64())

    override fun ping() =
        SelfTestCheck(
            "pdl-fullmakt-api via $url",
            false,
        ) {
            HealthCheckUtils.pingUrl(UrlUtils.joinPaths(url, "/internal/health/liveness"), client)
        }
}
