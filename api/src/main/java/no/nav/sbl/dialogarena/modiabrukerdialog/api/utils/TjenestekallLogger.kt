package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils

import org.slf4j.LoggerFactory
import java.lang.StringBuilder

private val tjenestekallLogg = LoggerFactory.getLogger("SecureLog")
object TjenestekallLogger {
    fun info(header: String, fields: Map<String, Any?>) = tjenestekallLogg.info(format(header, fields))
    fun error(header: String, fields: Map<String, Any?>) = tjenestekallLogg.error(format(header, fields))
    fun error(header: String, fields: Map<String, Any?>, throwable: Throwable) = tjenestekallLogg.error(format(header, fields), throwable)

    private fun format(header: String, fields: Map<String, Any?>): String {
        val sb = StringBuilder()
        sb.appendln(header)
        sb.appendln("------------------------------------------------------------------------------------")
        fields.forEach { (key, value) ->
            sb.appendln("$key: $value")
        }
        sb.appendln("------------------------------------------------------------------------------------")
        return sb.toString()
    }
}
