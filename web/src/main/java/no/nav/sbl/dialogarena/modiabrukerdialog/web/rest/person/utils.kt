package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person

import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ApplicationContextBeans
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val logger = LoggerFactory.getLogger(ApplicationContextBeans::class.java)

private val formatter = DateTimeFormatter.ISO_LOCAL_DATE
fun formatDate(d: LocalDateTime): String = d.format(formatter)
fun formatDate(d: LocalDate): String = d.format(formatter)

fun <T> tryOf(message: String, block: () -> T): T? {
    return try {
        block()
    } catch (e: java.lang.Exception) {
        logger.error(message, e)
        null
    }
}
