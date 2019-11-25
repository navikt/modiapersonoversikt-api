package no.nav.sbl.dialogarena.naudit

import no.nav.common.auth.SubjectHandler
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("AuditLogger")

class Audit {
    open class AuditResource(val resource: String)
    enum class Action {
        CREATE, READ, UPDATE, DELETE
    }

    interface AuditDescriptor<T> {
        fun log(resource: T)
    }

    internal class WithDataDescriptor<T>(
            private val action: Action,
            private val resourceType: AuditResource,
            private val extractIdentifiers: (T) -> Array<Pair<String, String?>>
    ) : AuditDescriptor<T> {
        override fun log(resource: T) {
            val identifiers = extractIdentifiers(resource)
            logInternal(action, resourceType, identifiers)
        }
    }

    internal class NoopDescriptor<T> : AuditDescriptor<T> {
        override fun log(resource: T) {}
    }

    internal class NothingDescriptor(
            private val action: Action,
            private val resourceType: AuditResource,
            private val identifiers: Array<out Pair<String, String?>>) : AuditDescriptor<Any> {
        override fun log(resource: Any) {
            logInternal(action, resourceType, identifiers)
        }
    }

    companion object {
        val skipAuditLog: AuditDescriptor<Any> = NoopDescriptor()

        @JvmStatic
        fun <T> skipAuditLog(): AuditDescriptor<T> = NoopDescriptor()

        @JvmStatic
        fun describe(action: Action, resourceType: AuditResource, vararg identifiers: Pair<String, String?>): AuditDescriptor<Any> {
            return NothingDescriptor(action, resourceType, identifiers)
        }

        @JvmStatic
        fun <T> describe(action: Action, resourceType: AuditResource, extractIdentifiers: (T) -> Array<Pair<String, String?>>): AuditDescriptor<T> {
            return WithDataDescriptor(action, resourceType, extractIdentifiers)
        }

        private fun logInternal(action: Action, resourceType: AuditResource, identifiers: Array<out Pair<String, String?>>) {
            SubjectHandler.getIdentType().orElse(null)
            val logline = listOf(
                    "action='$action' ",
                    SubjectHandler
                            .getIdent()
                            .map { "subject='$it' " }
                            .orElse(null),
                    "resource='${resourceType.resource}' ",
                    *identifiers
                            .map { "${it.first}='${it.second ?: "-"}'" }
                            .toTypedArray()
            )
                    .filterNotNull()
                    .joinToString(" ")

            log.info(logline)
        }
    }
}