package no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving

import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving.api.generated.apis.KravdetaljerApi
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

interface SkatteetatenInnkrevingClient {
    fun getKravdetaljer(
        kravidentifikator: String,
        kravidentifikatorType: KravidentifikatorType,
    ): Result<Unit>
}

@Component
class SkatteetatenInnkrevingHttpClient(
    private val kravdetaljerApi: KravdetaljerApi,
    @Value("\${skatteetaten.api.client.id}") private val clientId: String,
) : SkatteetatenInnkrevingClient, Pingable {

    override fun getKravdetaljer(
        kravidentifikator: String,
        kravidentifikatorType: KravidentifikatorType,
    ): Result<Unit> =
        runCatching {
            kravdetaljerApi.getKravdetaljer(
                klientid = clientId,
                accept = "application/json",
                kravidentifikator = kravidentifikator,
                kravidentifikatortype = kravidentifikatorType.name,
            )
        }

    override fun ping(): SelfTestCheck =
        SelfTestCheck("SkatteetatenInnkrevingHttpClient", false) {
            // Midlertidig ping som kun fungerer i test.
            getKravdetaljer(
                "99ea4fbc-9777-4fdf-8d8d-75c76a5a45e0",
                KravidentifikatorType.SKATTEETATENS_KRAVIDENTIFIKATOR,
            ).fold(
                { HealthCheckResult.healthy() },
                { HealthCheckResult.unhealthy(it) },
            )
        }
}
