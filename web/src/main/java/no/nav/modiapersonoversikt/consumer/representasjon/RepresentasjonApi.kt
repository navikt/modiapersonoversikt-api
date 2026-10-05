package no.nav.modiapersonoversikt.consumer.representasjon

import no.nav.common.health.HealthCheckUtils
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.types.identer.Fnr
import no.nav.common.utils.UrlUtils
import no.nav.modiapersonoversikt.consumer.representasjon.generated.apis.FullmaktApi
import no.nav.modiapersonoversikt.consumer.representasjon.generated.infrastructure.ClientException
import no.nav.modiapersonoversikt.consumer.representasjon.generated.models.FolkeregisterIdentRequestDto
import no.nav.modiapersonoversikt.consumer.representasjon.generated.models.FullmaktDto
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import okhttp3.OkHttpClient
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable

interface RepresentasjonApi : Pingable {
    fun hentFullmakterForFullmaktsgiver(fnr: Fnr): List<FullmaktDto>
}

@CacheConfig(cacheNames = ["representasjonApiCache"], keyGenerator = "userkeygenerator")
open class RepresentasjonApiImpl(
    private val url: String,
    private val client: OkHttpClient,
) : RepresentasjonApi {
    private val api = FullmaktApi(url, client)

    @Cacheable
    override fun hentFullmakterForFullmaktsgiver(fnr: Fnr): List<FullmaktDto> =
        try {
            requireNotNull(
                api.hentAlleFullmakterMedEndringerHvorIdentErFullmaktsgiver(
                    FolkeregisterIdentRequestDto(fnr.get()),
                    true,
                ),
            ) { "repr-api returnerte tom respons for fullmaktsgiver" }
        } catch (e: ClientException) {
            // Om personen ikke finnes i PDL så returneres 404
            if (e.statusCode == 404) emptyList() else throw e
        }

    override fun ping() =
        SelfTestCheck("repr-api via $url", false) {
            HealthCheckUtils.pingUrl(UrlUtils.joinPaths(url, "/actuator/health/liveness"), client)
        }
}
