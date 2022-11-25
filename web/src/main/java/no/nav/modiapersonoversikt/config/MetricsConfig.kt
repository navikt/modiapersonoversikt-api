package no.nav.modiapersonoversikt.config

import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.common.rest.client.RestClient
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.utils.MaskingUtils
import okhttp3.Request
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
open class MetricsConfig {
    @Autowired
    lateinit var prometheusMeterRegistry: PrometheusMeterRegistry

    @Autowired
    lateinit var unleash: UnleashService

    @PostConstruct
    fun setup() {
        if (unleash.isEnabled(Feature.USE_REST_CLIENT_METRICS)) {
            initRestClientWithMetricsListener()
        }
    }

    private fun initRestClientWithMetricsListener() {
        RestClient.setBaseClient(
            RestClient.baseClientBuilder().eventListener(
                buildEventListener().uriMapper(::buildUriMap)
                    .build()
            ).build()
        )
    }

    private fun buildEventListener(): OkHttpMetricsEventListener.Builder {
        return OkHttpMetricsEventListener.builder(prometheusMeterRegistry, "okhttp.requests")
    }

    private fun buildUriMap(req: Request): String {
        return MaskingUtils.maskSensitiveInfo(req.url().encodedPath())
    }
}
