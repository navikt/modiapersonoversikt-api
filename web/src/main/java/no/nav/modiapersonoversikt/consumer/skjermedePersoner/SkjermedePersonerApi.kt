package no.nav.modiapersonoversikt.consumer.skjermedePersoner

import com.github.benmanes.caffeine.cache.Cache
import no.nav.common.health.HealthCheckUtils
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.types.identer.Fnr
import no.nav.common.utils.UrlUtils
import no.nav.modiapersonoversikt.consumer.skjermedePersonerPip.generated.apis.SkjermingPipApi
import no.nav.modiapersonoversikt.consumer.skjermedePersonerPip.generated.models.SkjermetDataRequestDTO
import no.nav.modiapersonoversikt.infrastructure.cache.CacheUtils
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import okhttp3.OkHttpClient

interface SkjermedePersonerApi : Pingable {
    fun erSkjermetPerson(fnr: Fnr): Boolean
}

class SkjermedePersonerApiImpl(
    private val url: String,
    private val client: OkHttpClient,
    private val cache: Cache<Fnr, Boolean> = CacheUtils.createCache(),
) : SkjermedePersonerApi {
    private val skjermingPipApi = SkjermingPipApi(url, client)

    override fun erSkjermetPerson(fnr: Fnr): Boolean =
        requireNotNull(
            cache.get(fnr) {
                skjermingPipApi.isSkjermetPostUsingPOST(SkjermetDataRequestDTO(fnr.get()))
            },
        )

    override fun ping() =
        SelfTestCheck(
            "SkjermedePersonerApi via $url",
            false,
        ) {
            HealthCheckUtils.pingUrl(UrlUtils.joinPaths(url, "/internal/health/liveness"), client)
        }
}
