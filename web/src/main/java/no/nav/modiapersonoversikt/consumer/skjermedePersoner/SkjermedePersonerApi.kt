package no.nav.modiapersonoversikt.consumer.skjermedePersoner

import no.nav.common.health.HealthCheckUtils
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.utils.UrlUtils
import no.nav.modiapersonoversikt.infrastructure.types.Pingable
import no.nav.modiapersonoversikt.legacy.api.domain.skjermedePersonerPip.generated.apis.SkjermingPipApi
import no.nav.modiapersonoversikt.legacy.api.domain.skjermedePersonerPip.generated.models.SkjermetDataRequestDTO
import okhttp3.OkHttpClient

interface SkjermedePersonerApi : Pingable {
    fun erSkjermetPerson(fnr: String): Boolean
}

class SkjermedePersonerApiImpl(
    private val url: String,
    private val client: OkHttpClient
) : SkjermedePersonerApi {
    val skjermingPipApi = SkjermingPipApi(url, client)

    override fun erSkjermetPerson(fnr: String): Boolean {
        return skjermingPipApi.isSkjermetPostUsingPOST(SkjermetDataRequestDTO(fnr))
    }

    override fun ping() = SelfTestCheck(
        "SkjermedePersonerApi via $url",
        false
    ) {
        HealthCheckUtils.pingUrl(UrlUtils.joinPaths(url, "/internal/health/liveness"), client)
    }
}
