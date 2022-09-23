package no.nav.modiapersonoversikt.infrastructure

import net.logstash.logback.marker.Markers
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker

object TjenestekallLogger {
    private val tjenestekallLogg = LoggerFactory.getLogger("SecureLog")
    const val LOGTYPE = "logtype"
    enum class Level {
        INFO, WARN, ERROR
    }

    fun raw(
        level: Level,
        header: String,
        fields: Map<String, Any?>,
        tags: Map<String, Any?> = emptyMap(),
        exception: Throwable? = null,
    ) {
        val message = format(header, fields)
        val logcall: (Marker, String, Throwable?) -> Unit = when (level) {
            Level.INFO -> tjenestekallLogg::info
            Level.WARN -> tjenestekallLogg::warn
            Level.ERROR -> tjenestekallLogg::error
        }
        logcall(Markers.appendEntries(tags), message, exception)
    }

    fun info(header: String, fields: Map<String, Any?>, tags: Map<String, Any?> = emptyMap()) = raw(Level.INFO, header, fields, tags)
    fun warn(header: String, fields: Map<String, Any?>, tags: Map<String, Any?> = emptyMap()) = raw(Level.WARN, header, fields, tags)
    fun error(header: String, fields: Map<String, Any?>, tags: Map<String, Any?> = emptyMap()) = raw(Level.ERROR, header, fields, tags)
    fun error(header: String, fields: Map<String, Any?>, tags: Map<String, Any?> = emptyMap(), throwable: Throwable) =
        raw(Level.ERROR, header, fields, tags, throwable)

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
