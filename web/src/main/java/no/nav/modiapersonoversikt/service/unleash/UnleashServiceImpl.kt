package no.nav.modiapersonoversikt.service.unleash

import io.getunleash.Unleash
import io.getunleash.event.ClientFeaturesResponse
import io.getunleash.repository.HttpFeatureFetcher
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.utils.fn.UnsafeRunnable
import no.nav.modiapersonoversikt.infrastructure.ping.ConsumerPingable

class UnleashServiceImpl(
    private val toggleFetcher: HttpFeatureFetcher,
    private val defaultUnleash: Unleash,
    var api: String?,
) : UnleashService {
    private val pingDelegate: ConsumerPingable =
        ConsumerPingable(
            "Unleash",
            false,
            UnsafeRunnable {
                val featuresResponse: ClientFeaturesResponse = this.toggleFetcher.fetchFeatures()
                val status: ClientFeaturesResponse.Status = featuresResponse.status
                if (status == ClientFeaturesResponse.Status.UNAVAILABLE) {
                    throw java.net.ConnectException("Ping mot Unleash på $api. Ga status $status")
                }
            },
        )

    override fun isEnabled(feature: no.nav.modiapersonoversikt.service.unleash.Feature): kotlin.Boolean =
        defaultUnleash.isEnabled(feature.propertyKey)

    override fun isEnabled(feature: kotlin.String): kotlin.Boolean = defaultUnleash.isEnabled(feature)

    override fun ping(): SelfTestCheck? = pingDelegate.ping()

    companion object {
        private val log: org.slf4j.Logger? = org.slf4j.LoggerFactory.getLogger(UnleashServiceImpl::class.java)
    }
}
