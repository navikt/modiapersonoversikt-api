package no.nav.modiapersonoversikt.consumer.representasjon

import no.nav.common.health.HealthCheckUtils
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.types.identer.Fnr
import no.nav.common.utils.UrlUtils
import no.nav.modiapersonoversikt.consumer.reprApi.generated.apis.FullmaktApi
import no.nav.modiapersonoversikt.consumer.reprApi.generated.infrastructure.ClientException
import no.nav.modiapersonoversikt.consumer.reprApi.generated.models.FolkeregisterIdentRequestDto
import no.nav.modiapersonoversikt.consumer.reprApi.generated.models.FullmaktDto
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import okhttp3.OkHttpClient
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable

interface ReprApi : Pingable {
    fun hentfullmakterforfullmaktsgiver(fnr: Fnr): List<FullmaktDto>
}

@CacheConfig(cacheNames = ["reprApiCache"], keyGenerator = "userkeygenerator")
open class ReprApiImpl(
    private val url: String,
    private val client: OkHttpClient,
) : ReprApi {
    private val api = FullmaktApi(url, client)

    @Cacheable
    override fun hentfullmakterforfullmaktsgiver(fnr: Fnr): List<FullmaktDto> =
        try {
            requireNotNull(
                api.hentAlleFullmakterMedEndringerHvorIdentErFullmaktsgiver(
                    FolkeregisterIdentRequestDto(fnr.get()),
                    true,
                ),
            ) { "repr-api returnerte tom respons for fullmaktsgiver" }
        } catch (e: ClientException) {
            if (e.statusCode == 404) emptyList() else throw e
        }

    override fun ping() =
        SelfTestCheck("repr-api via $url", false) {
            HealthCheckUtils.pingUrl(UrlUtils.joinPaths(url, "/actuator/health/liveness"), client)
        }
}
