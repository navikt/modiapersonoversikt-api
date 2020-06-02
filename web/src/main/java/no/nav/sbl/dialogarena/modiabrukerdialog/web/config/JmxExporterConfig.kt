package no.nav.sbl.dialogarena.modiabrukerdialog.web.config

import com.google.gson.GsonBuilder
import io.prometheus.client.Collector
import io.prometheus.jmx.JmxCollector

object JmxExporterConfig {
    val config = mapOf(
            "rules" to listOf(
                    mapOf(
                            "pattern" to "type=CacheStatistics.*?Cache(?:Miss|Hit)"
                    )
            )
    )

    @JvmStatic
    fun setup() {
        val gson = GsonBuilder().disableHtmlEscaping().create()
        JmxCollector(gson.toJson(config)).register<Collector>()
    }
}
