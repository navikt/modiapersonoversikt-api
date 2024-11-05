package no.nav.modiapersonoversikt.consumer.pdlFullmaktApi

import no.nav.common.health.HealthCheckUtils
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.types.identer.Fnr
import no.nav.common.utils.UrlUtils
import no.nav.modiapersonoversikt.consumer.pdlFullmaktApi.generated.apis.FullmaktInternbrukerAzureADApi
import no.nav.modiapersonoversikt.consumer.pdlFullmaktApi.generated.models.FullmaktDto
import no.nav.modiapersonoversikt.consumer.pdlFullmaktApi.generated.models.RequestDto
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import okhttp3.OkHttpClient
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable

interface PdlFullmaktApi : Pingable {
    fun hentFullmakterForFullmektig(fnr: Fnr): List<FullmaktDto>?

    fun hentFullmakterForFullmaktsgiver(fnr: Fnr): List<FullmaktDto>?
}

@CacheConfig(cacheNames = ["pdlFullmaktCache"], keyGenerator = "userkeygenerator")
open class PdlFullmaktApiImpl(
    private val url: String,
    private val client: OkHttpClient,
) : PdlFullmaktApi {
    private val pdlFullmaktApi = FullmaktInternbrukerAzureADApi(url, client)

    @Cacheable
    override fun hentFullmakterForFullmektig(fnr: Fnr): List<FullmaktDto>? =
        pdlFullmaktApi.hentAktiveFullmakterForFullmektig(fullmaktRequest(fnr.get()))

    @Cacheable
    override fun hentFullmakterForFullmaktsgiver(fnr: Fnr): List<FullmaktDto>? =
        pdlFullmaktApi.hentAktiveFullmakterForFullmaktsgiver(fullmaktRequest(fnr.get()))

    private fun fullmaktRequest(ident: String) = RequestDto(ident)

    override fun ping() =
        SelfTestCheck(
            "pdl-fullmakt-api via $url",
            false,
        ) {
            HealthCheckUtils.pingUrl(UrlUtils.joinPaths(url, "/internal/health/liveness"), client)
        }
}
