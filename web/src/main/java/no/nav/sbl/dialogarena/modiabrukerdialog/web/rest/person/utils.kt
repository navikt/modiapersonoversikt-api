package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person

import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ApplicationContextBeans
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.util.*

private val logger = LoggerFactory.getLogger(ApplicationContextBeans::class.java)

fun formatDate(d: Date): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    return formatter.format(d)
}

fun <T> tryOf(message: String, block: () -> T): T? {
    return try {
        block()
    } catch (e: java.lang.Exception) {
        logger.error(message, e)
        null
    }
}
