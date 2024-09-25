package no.nav.modiapersonoversiktproxy.infrastructure.naudit

import net.logstash.logback.marker.Markers
import no.nav.modiapersonoversiktproxy.infrastructure.AuthContextUtils
import no.nav.modiapersonoversiktproxy.infrastructure.naudit.AuditIdentifier.DENY_REASON
import no.nav.modiapersonoversiktproxy.infrastructure.naudit.AuditIdentifier.FAIL_REASON
import no.nav.personoversikt.common.logging.Logging

val cefLogger =
    ArchSightCEFLogger(
        CEFLoggerConfig(
            applicationName = "modia",
            logName = "personoversikt",
            filter = { (action: Audit.Action, resource: Audit.AuditResource) ->
                action != Audit.Action.READ || resource == AuditResources.Person.Personalia
            },
        ),
    )

class Audit {
    open class AuditResource(
        val resource: String,
    )

    enum class Action {
        CREATE,
        READ,
        UPDATE,
        DELETE,
    }

    interface AuditDescriptor<T> {
        fun log(resource: T)

        fun denied(reason: String)

        fun failed(exception: Throwable)

        fun Throwable.getFailureReason(): String = this.message ?: this.toString()
    }

    internal class WithDataDescriptor<T>(
        private val action: Action,
        private val resourceType: AuditResource,
        private val extractIdentifiers: (T?) -> List<Pair<AuditIdentifier, String?>>,
    ) : AuditDescriptor<T> {
        override fun log(resource: T) {
            val identifiers = extractIdentifiers(resource).toTypedArray()
            logInternal(action, resourceType, identifiers)
        }

        override fun denied(reason: String) {
            val identifiers = extractIdentifiers(null).toTypedArray().plus(DENY_REASON to reason)
            logInternal(action, resourceType, identifiers)
        }

        override fun failed(exception: Throwable) {
            val identifiers = extractIdentifiers(null).toTypedArray().plus(FAIL_REASON to exception.getFailureReason())
            logInternal(action, resourceType, identifiers)
        }
    }

    companion object {
        @JvmStatic
        fun <T> describe(
            action: Action,
            resourceType: AuditResource,
            extractIdentifiers: (T?) -> List<Pair<AuditIdentifier, String?>>,
        ): AuditDescriptor<T> = WithDataDescriptor(action, resourceType, extractIdentifiers)

        private val auditMarker = Markers.appendEntries(mapOf(Logging.LOGTYPE_KEY to "audit"))

        private fun logInternal(
            action: Action,
            resourceType: AuditResource,
            identifiers: Array<Pair<AuditIdentifier, String?>>,
        ) {
            val subject = AuthContextUtils.getIdent()
            val logline =
                listOfNotNull(
                    "action='$action'",
                    subject
                        .map { "subject='$it'" }
                        .orElse(null),
                    "resource='${resourceType.resource}'",
                    *identifiers
                        .map { "${it.first}='${it.second ?: "-"}'" }
                        .toTypedArray(),
                ).joinToString(" ")

            Logging.secureLog.info(auditMarker, logline)
            cefLogger.log(CEFEvent(action, resourceType, subject.orElse("-"), identifiers))
        }
    }
}
