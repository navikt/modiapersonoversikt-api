package no.nav.modiapersonoversikt.config

import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import io.prometheus.metrics.model.registry.PrometheusRegistry
import no.nav.common.rest.client.RestClient
import no.nav.modiapersonoversikt.utils.UrlMaskingUtils
import okhttp3.Request
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MetricsConfig {
    companion object {
        private val collectorRegistry = PrometheusRegistry()

        @JvmStatic
        fun setup() {
            RestClient.setBaseClient(
                RestClient
                    .baseClientBuilder()
                    .eventListener(
                        buildEventListener()
                            .uriMapper(::buildUriMap)
                            .build(),
                    ).build(),
            )
        }

        private fun buildEventListener(): OkHttpMetricsEventListener.Builder {
            val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT, collectorRegistry, Clock.SYSTEM)
            return OkHttpMetricsEventListener.builder(registry, "okhttp.requests")
        }

        private fun buildUriMap(req: Request): String = UrlMaskingUtils.maskSensitiveInfo(req.url.encodedPath)
    }

    @Bean
    open fun collectorRegistry(): PrometheusRegistry = collectorRegistry
}
