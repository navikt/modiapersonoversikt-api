package no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving

import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving.api.generated.apis.KravdetaljerApi
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
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
    private val unleashService: UnleashService,
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
            if (!unleashService.isEnabled(Feature.SKATTEETATEN_INNKREVING_API)) {
                return@SelfTestCheck HealthCheckResult.unhealthy("Feature toggled off")
            }

            // Midlertidig ping som kun fungerer i test.
            getKravdetaljer(
                "87b5a5c6-17ea-413a-ad80-b6c3406188fa",
                KravidentifikatorType.SKATTEETATENS_KRAVIDENTIFIKATOR,
            ).fold(
                { HealthCheckResult.healthy() },
                { HealthCheckResult.unhealthy(it) },
            )
        }
}
