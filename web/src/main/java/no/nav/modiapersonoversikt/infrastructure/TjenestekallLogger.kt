package no.nav.modiapersonoversikt.infrastructure

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object TjenestekallLogger {
    private val tjenestekallLogg = LoggerFactory.getLogger("SecureLog")

    fun info(header: String, fields: Map<String, Any?>) = tjenestekallLogg.info(format(header, fields))
    fun warn(header: String, fields: Map<String, Any?>) = tjenestekallLogg.warn(format(header, fields))
    fun error(header: String, fields: Map<String, Any?>) = tjenestekallLogg.error(format(header, fields))
    fun error(header: String, fields: Map<String, Any?>, throwable: Throwable) =
        tjenestekallLogg.error(format(header, fields), throwable)

    val logger: Logger = tjenestekallLogg

    fun format(header: String, fields: Map<String, Any?>): String {
        val sb = StringBuilder()
        sb.appendLine(header)
        sb.appendLine("------------------------------------------------------------------------------------")
        fields.forEach { (key, value) ->
            sb.appendLine("$key: $value")
        }
        sb.appendLine("------------------------------------------------------------------------------------")
        return sb.toString()
    }

    fun format(header: String, body: String): String {
        val sb = StringBuilder()
        sb.appendLine(header)
        sb.appendLine("------------------------------------------------------------------------------------")
        sb.appendLine(body)
        sb.appendLine("------------------------------------------------------------------------------------")
        return sb.toString()
    }
}
