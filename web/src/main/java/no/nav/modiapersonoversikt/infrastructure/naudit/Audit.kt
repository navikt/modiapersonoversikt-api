package no.nav.modiapersonoversikt.infrastructure.naudit

import net.logstash.logback.marker.Markers
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.TjenestekallLogger
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier.DENY_REASON
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier.FAIL_REASON
import org.slf4j.LoggerFactory

private val tjenestekallLogg = LoggerFactory.getLogger("SecureLog")
val cefLogger = ArchSightCEFLogger(
    CEFLoggerConfig(
        applicationName = "modia",
        logName = "personoversikt",
        filter = { (action: Audit.Action, resource: Audit.AuditResource) ->
            action != Audit.Action.READ || resource == AuditResources.Person.Personalia
        }
    )
)

class Audit {
    open class AuditResource(val resource: String)
    enum class Action {
        CREATE, READ, UPDATE, DELETE
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
        private val extractIdentifiers: (T?) -> List<Pair<AuditIdentifier, String?>>
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

    internal class NoopDescriptor<T> : AuditDescriptor<T> {
        override fun log(resource: T) {}
        override fun denied(reason: String) {}
        override fun failed(exception: Throwable) {}
    }

    internal class NothingDescriptor(
        private val action: Action,
        private val resourceType: AuditResource,
        private val identifiers: Array<Pair<AuditIdentifier, String?>>
    ) : AuditDescriptor<Any> {
        override fun log(resource: Any) {
            logInternal(action, resourceType, identifiers)
        }

        override fun denied(reason: String) {
            logInternal(action, resourceType, identifiers.plus(DENY_REASON to reason))
        }

        override fun failed(exception: Throwable) {
            logInternal(action, resourceType, identifiers.plus(FAIL_REASON to exception.getFailureReason()))
        }
    }

    companion object {
        val skipAuditLog: AuditDescriptor<Any> = NoopDescriptor()

        @JvmStatic
        fun <T> skipAuditLog(): AuditDescriptor<T> = NoopDescriptor()

        @JvmStatic
        fun describe(action: Action, resourceType: AuditResource, vararg identifiers: Pair<AuditIdentifier, String?>): AuditDescriptor<Any> {
            return NothingDescriptor(action, resourceType, identifiers as Array<Pair<AuditIdentifier, String?>>)
        }

        @JvmStatic
        fun <T> describe(action: Action, resourceType: AuditResource, extractIdentifiers: (T?) -> List<Pair<AuditIdentifier, String?>>): AuditDescriptor<T> {
            return WithDataDescriptor(action, resourceType, extractIdentifiers)
        }

        private val auditMarker = Markers.appendEntries(mapOf(TjenestekallLogger.LOGTYPE to "audit"))
        private fun logInternal(action: Action, resourceType: AuditResource, identifiers: Array<Pair<AuditIdentifier, String?>>) {
            val subject = AuthContextUtils.getIdent()
            val logline = listOfNotNull(
                "action='$action'",
                subject
                    .map { "subject='$it'" }
                    .orElse(null),
                "resource='${resourceType.resource}'",
                *identifiers
                    .map { "${it.first}='${it.second ?: "-"}'" }
                    .toTypedArray()
            )
                .joinToString(" ")

            tjenestekallLogg.info(auditMarker, logline)
            cefLogger.log(CEFEvent(action, resourceType, subject.orElse("-"), identifiers))
        }
    }
}
