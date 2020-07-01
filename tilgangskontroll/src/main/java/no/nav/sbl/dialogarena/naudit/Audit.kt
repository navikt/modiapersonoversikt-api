package no.nav.sbl.dialogarena.naudit

import no.nav.common.auth.SubjectHandler
import no.nav.sbl.dialogarena.naudit.AuditIdentifier.DENY_REASON
import org.slf4j.LoggerFactory

private val tjenestekallLogg = LoggerFactory.getLogger("SecureLog")
val cefLogger = ArchSightCEFLogger(CEFLoggerConfig(
        applicationName = "modia",
        logName = "personoversikt",
        filter = { (action: Audit.Action, resource: Audit.AuditResource) ->
            action != Audit.Action.READ || resource == AuditResources.Person.Personalia
        }
))

class Audit {
    open class AuditResource(val resource: String)
    enum class Action {
        CREATE, READ, UPDATE, DELETE
    }

    interface AuditDescriptor<T> {
        fun log(resource: T)
        fun denied(reason: String)
    }

    internal class WithDataDescriptor<T>(
            private val action: Action,
            private val resourceType: AuditResource,
            private val extractIdentifiers: (T) -> List<Pair<AuditIdentifier, String?>>
    ) : AuditDescriptor<T> {
        override fun log(resource: T) {
            val identifiers = extractIdentifiers(resource).toTypedArray()
            logInternal(action, resourceType, identifiers)
        }

        override fun denied(reason: String) {
            logInternal(action, resourceType, arrayOf(DENY_REASON to reason))
        }
    }

    internal class NoopDescriptor<T> : AuditDescriptor<T> {
        override fun log(resource: T) {}
        override fun denied(reason: String) {}
    }

    internal class NothingDescriptor(
            private val action: Action,
            private val resourceType: AuditResource,
            private val identifiers: Array<out Pair<AuditIdentifier, String?>>) : AuditDescriptor<Any> {
        override fun log(resource: Any) {
            logInternal(action, resourceType, identifiers)
        }

        override fun denied(reason: String) {
            logInternal(action, resourceType, arrayOf(DENY_REASON to reason))
        }
    }

    companion object {
        val skipAuditLog: AuditDescriptor<Any> = NoopDescriptor()

        @JvmStatic
        fun <T> skipAuditLog(): AuditDescriptor<T> = NoopDescriptor()

        @JvmStatic
        fun describe(action: Action, resourceType: AuditResource, vararg identifiers: Pair<AuditIdentifier, String?>): AuditDescriptor<Any> {
            return NothingDescriptor(action, resourceType, identifiers)
        }

        @JvmStatic
        fun <T> describe(action: Action, resourceType: AuditResource, extractIdentifiers: (T) -> List<Pair<AuditIdentifier, String?>>): AuditDescriptor<T> {
            return WithDataDescriptor(action, resourceType, extractIdentifiers)
        }

        private fun logInternal(action: Action, resourceType: AuditResource, identifiers: Array<out Pair<AuditIdentifier, String?>>) {
            val subject = SubjectHandler.getIdent()
            val logline = listOf(
                    "action='$action'",
                    subject
                            .map { "subject='$it'" }
                            .orElse(null),
                    "resource='${resourceType.resource}'",
                    *identifiers
                            .map { "${it.first}='${it.second ?: "-"}'" }
                            .toTypedArray()
            )
                    .filterNotNull()
                    .joinToString(" ")

            tjenestekallLogg.info(logline)
            cefLogger.log(CEFEvent(action, resourceType, subject.orElse("-"), identifiers))
        }
    }
}
