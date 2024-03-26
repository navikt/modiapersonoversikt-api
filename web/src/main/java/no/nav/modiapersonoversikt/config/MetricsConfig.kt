package no.nav.modiapersonoversikt.config

import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.prometheus.client.CollectorRegistry
import no.nav.common.rest.client.RestClient
import no.nav.modiapersonoversikt.utils.UrlMaskingUtils
import okhttp3.Request
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MetricsConfig() {
    companion object {
        private val collectorRegistry = CollectorRegistry(true)

        @JvmStatic
        fun setup() {
            RestClient.setBaseClient(
                RestClient.baseClientBuilder().eventListener(
                    buildEventListener().uriMapper(::buildUriMap)
                        .build(),
                ).build(),
            )
        }

        private fun buildEventListener(): OkHttpMetricsEventListener.Builder {
            val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT, collectorRegistry, Clock.SYSTEM)
            return OkHttpMetricsEventListener.builder(registry, "okhttp.requests")
        }

        private fun buildUriMap(req: Request): String {
            return UrlMaskingUtils.maskSensitiveInfo(req.url.encodedPath)
        }
    }

    @Bean
    open fun collectorRegistry(): CollectorRegistry = collectorRegistry
}
