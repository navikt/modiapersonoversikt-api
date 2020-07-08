package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util

import org.slf4j.LoggerFactory
import java.lang.StringBuilder

private val tjenestekallLogg = LoggerFactory.getLogger("SecureLog")
object TjenestekallLogger {
    fun info(header: String, fields: Map<String, Any?>) = tjenestekallLogg.info(format(header, fields))
    fun error(header: String, fields: Map<String, Any?>) = tjenestekallLogg.error(format(header, fields))
    fun error(header: String, fields: Map<String, Any?>, throwable: Throwable) = tjenestekallLogg.error(format(header, fields), throwable)

    private fun format(header: String, fields: Map<String, Any?>): String {
        val sb = StringBuilder()
        sb.append(header)
        sb.append("------------------------------------------------------------------------------------")
        fields.forEach { (key, value) ->
            sb.append("    ${key}: $value")
        }
        sb.append("------------------------------------------------------------------------------------")
        return sb.toString()
    }
}

