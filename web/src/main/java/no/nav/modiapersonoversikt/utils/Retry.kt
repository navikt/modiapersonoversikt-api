package no.nav.modiapersonoversikt.utils

import org.slf4j.LoggerFactory
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.pow

class Retry(val config: Config) {

    data class Config(
        val maxRetries: Int = Int.MAX_VALUE,
        val initDelay: Long,
        val growthFactor: Double,
        val delayLimit: Long,
        val scheduler: Timer = Timer()
    )

    private val log = LoggerFactory.getLogger(Retry::class.java)

    fun run(block: () -> Unit) = run(0, block)

    private fun run(attemptNumber: Int, block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            val delay =
                (config.initDelay * config.growthFactor.pow(attemptNumber)).toLong().coerceAtMost(config.delayLimit)

            log.error("Retry failed at attempt $attemptNumber with error: ${e.message}")

            if (attemptNumber < config.maxRetries) {
                config.scheduler.schedule(delay) {
                    run(attemptNumber + 1, block)
                }
            }
        }
    }
}
