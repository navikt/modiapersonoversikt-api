package no.modiapersonoversikt.common

import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import io.prometheus.metrics.model.registry.PrometheusRegistry
import no.nav.common.rest.client.RestClient
import okhttp3.Request

open class MetricsConfig() {
    companion object {
        private val collectorRegistry = PrometheusRegistry()

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
}
