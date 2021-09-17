package no.nav.modiapersonoversikt.utils

import org.slf4j.Logger
import java.util.*
import kotlin.concurrent.schedule

object ScheduleUtils {

    fun <T> retry(
        times: Int = Int.MAX_VALUE,
        initDelay: Long = 100,
        factor: Double = 2.0,
        delayLimit: Long = 1000,
        scheduler: Timer = Timer(),
        logger: Logger? = null,
        logMessage: String = "",
        block: () -> T
    ): T {
        var currentDelay = initDelay
        var attemptNo = 0
        repeat(times) {
            attemptNo++
            try {
                return block()
            } catch (e: Exception) {
                logger?.error("'$logMessage' at attempt ${attemptNo} with error: ${e.message}")
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(delayLimit)
                scheduler.schedule(delay = currentDelay) {
                    block()
                }
            }
        }
        return block()
    }

}