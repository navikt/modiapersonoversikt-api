package no.nav.modiapersonoversikt.infrastructure.naudit

import org.slf4j.LoggerFactory
import java.time.Instant

private val auditLogg = LoggerFactory.getLogger("AuditLogger")

enum class CEFSeverity {
    INFO,
    WARN,
}

fun escapeHeader(value: String): String =
    value
        .replace("\\", "\\\\")
        .replace("|", "\\|")

fun escapeAttribute(value: String): String =
    value
        .replace("\\", "\\\\")
        .replace("=", "\\=")

data class CEFLoggerConfig(
    val cefVersion: String = "0",
    val applicationName: String,
    val logName: String = "Leselogg",
    val logFormatVersion: String = "1.0",
    val eventType: String = "audit:access",
    val description: String = "SporingsLogger",
    val filter: (event: CEFEvent) -> Boolean = { true },
)

data class CEFEvent(
    val action: Audit.Action,
    val resource: Audit.AuditResource,
    val subject: String,
    val identifiers: Array<out Pair<AuditIdentifier, String?>>,
    val severity: CEFSeverity = CEFSeverity.INFO,
    val time: Long = Instant.now().toEpochMilli(),
)

enum class CEFAttributeName(
    val attribute: String,
) {
    TIME("end"),
    ACTION("act"),
    SUBJECT("suid"),
    RESOURCE("sproc"),
    RESOURCE_OWNER("duid"),
    STR1("flexString1"),
    STR1_LABEL("flexString1Label"),
    STR2("flexString2"),
    STR2_LABEL("flexString2Label"),
    STR3("cs3"),
    STR3_LABEL("cs3Label"),
    STR4("cs4"),
    STR4_LABEL("cs4Label"),
    STR5("cs5"),
    STR5_LABEL("cs5Label"),
    STR6("cs6"),
    STR6_LABEL("cs6Label"),
    ;

    companion object {
        fun getStringKey(id: Int): CEFAttributeName = valueOf("STR$id")

        fun getStringLabelKey(id: Int): CEFAttributeName = valueOf("STR${id}_LABEL")
    }
}

sealed class CEFAttributesType {
    data class EnumDescriptor(
        val attribute: CEFAttributeName,
        val value: String,
    ) : CEFAttributesType()

    data class StringDescriptor(
        val attribute: String,
        val value: String,
    ) : CEFAttributesType()
}

class CEFAttributes {
    private val attributes: MutableList<CEFAttributesType> = mutableListOf()

    fun add(
        attribute: CEFAttributeName,
        value: String,
    ): CEFAttributes {
        this.attributes.add(CEFAttributesType.EnumDescriptor(attribute, value))
        return this
    }

    fun addStringValue(
        attribute: AuditIdentifier,
        value: String,
    ): CEFAttributes {
        when (attribute) {
            AuditIdentifier.FNR -> this.add(CEFAttributeName.RESOURCE_OWNER, value)
            else -> this.attributes.add(CEFAttributesType.StringDescriptor(attribute.name, value))
        }
        return this
    }

    fun createCEFAttributes(): List<Pair<CEFAttributeName, String>> {
        var counter = 1
        return attributes
            .flatMap {
                when (it) {
                    is CEFAttributesType.EnumDescriptor -> listOf(it.attribute to it.value)
                    is CEFAttributesType.StringDescriptor ->
                        listOf(
                            CEFAttributeName.getStringKey(counter) to it.value,
                            CEFAttributeName.getStringLabelKey(counter++) to it.attribute,
                        )
                }
            }
    }
}

class ArchSightCEFLogger(
    private val config: CEFLoggerConfig,
) {
    private val descriptor: String =
        String.format(
            "CEF:%s|%s|%s|%s|%s|%s",
            escapeHeader(config.cefVersion),
            escapeHeader(config.applicationName),
            escapeHeader(config.logName),
            escapeHeader(config.logFormatVersion),
            escapeHeader(config.eventType),
            escapeHeader(config.description),
        )

    internal fun create(event: CEFEvent): String? {
        if (!config.filter(event)) {
            return null
        }
        val attributes = CEFAttributes()
        attributes.add(CEFAttributeName.TIME, event.time.toString())
        attributes.add(CEFAttributeName.ACTION, event.action.name)
        attributes.add(CEFAttributeName.SUBJECT, event.subject)
        attributes.add(CEFAttributeName.RESOURCE, event.resource.resource)
        event.identifiers.forEach { attributes.addStringValue(it.first, it.second ?: "-") }

        val extension =
            attributes
                .createCEFAttributes()
                .joinToString(" ") {
                    "${it.first.attribute}=${escapeAttribute(it.second)}"
                }

        return "$descriptor|${event.severity}|$extension"
    }

    fun log(event: CEFEvent) {
        create(event)?.also { auditLogg.info(it) }
    }
}
