package no.nav.sbl.dialogarena.naudit

import org.slf4j.LoggerFactory
import java.time.Instant

private val auditLogg = LoggerFactory.getLogger("AuditLogger")

enum class CEFSeverity {
    INFO, WARN;
}

fun escapeHeader(value: String): String = value
        .replace("\\", "\\\\")
        .replace("|", "\\|")

fun escapeAttribute(value: String): String = value
        .replace("\\", "\\\\")
        .replace("=", "\\=")

data class CEFLoggerConfig(
        val cefVersion: String = "0",
        val applicationName: String,
        val logName: String = "Leselogg",
        val logFormatVersion: String = "1.0",
        val eventType: String = "audit:access",
        val description: String = "SporingsLogger",
        val filter: (event: CEFEvent) -> Boolean = { true }
)

data class CEFEvent(
        val action: Audit.Action,
        val resource: Audit.AuditResource,
        val subject: String,
        val identifiers: Array<out Pair<AuditIdentifier, String?>>,
        val severity: CEFSeverity = CEFSeverity.INFO
)

enum class CEFAttribute(val attribute: String) {
    TIME("end"),
    ACTION("act"),
    SUBJECT("suid"),
    RESOURCE("name"),
    RESOURCE_OWNER("duid")
}

class ArchSightCEFLogger(private val config: CEFLoggerConfig) {
    private val descriptor: String = String.format(
            "CEF:%s|%s|%s|%s|%s|%s|{}|",
            escapeHeader(config.cefVersion),
            escapeHeader(config.applicationName),
            escapeHeader(config.logName),
            escapeHeader(config.logFormatVersion),
            escapeHeader(config.eventType),
            escapeHeader(config.description)
    )

    fun log(event: CEFEvent) {
        if (config.filter(event)) {
            val extension = arrayOf(
                    CEFAttribute.TIME to Instant.now().toEpochMilli().toString(),
                    CEFAttribute.ACTION to event.action.name,
                    CEFAttribute.SUBJECT to event.subject,
                    CEFAttribute.RESOURCE to event.resource.resource,
                    *event.identifiers
                            .find { it.first == AuditIdentifier.FNR }
                            ?.let { arrayOf(CEFAttribute.RESOURCE_OWNER to (it.second ?: "-")) }
                            ?: emptyArray()
            )
                    .map { "${it.first.attribute}=${escapeAttribute(it.second)}" }
                    .joinToString(" ")

            auditLogg.info(descriptor + extension, event.severity)
        }
    }
}
