package no.nav.modiapersonoversikt.infrastructure.scientist

import no.nav.modiapersonoversikt.legacy.api.utils.TjenestekallLogger
import kotlin.random.Random

typealias Reporter = (header: String, fields: Map<String, Any?>) -> Unit
object Scientist {
    private val defaultReporter: Reporter = { header, fields -> TjenestekallLogger.info(header, fields) }
    data class Config(
        val name: String,
        val experimentRate: Double,
        val reporter: Reporter = defaultReporter
    )

    data class TimedValue<T>(val value: T, val time: Long)
    private class Timer {
        private var startTime: Long = 0L
        fun start() {
            this.startTime = System.nanoTime()
        }

        fun elapsed(): Long {
            try {
                return System.nanoTime() - this.startTime
            } finally {
                this.startTime = 0L
            }
        }

        fun <T> time(block: () -> T): TimedValue<T> {
            this.start()
            val value = block()
            val time = this.elapsed()
            return TimedValue(value, time)
        }
    }

    class Experiment<T> internal constructor(private val config: Config) {
        private val timer = Timer()

        fun run(
            control: () -> T,
            experiment: () -> T
        ): T {
            if (Random.nextDouble() < config.experimentRate) {
                val fields = mutableMapOf<String, Any?>()
                val controlResult = timer.time(control)
                val experimentResult = runCatching {
                    timer.time(experiment)
                }

                if (experimentResult.isFailure) {
                    fields["ok"] = false
                    fields["control"] = controlResult
                    fields["exception"] = experimentResult.exceptionOrNull()
                } else {
                    val controlValue = controlResult.value
                    val experimentValue = experimentResult.getOrThrow().value
                    fields["ok"] = controlValue == experimentValue
                    fields["control"] = controlResult
                    fields["experiment"] = experimentResult.getOrThrow()
                }

                config.reporter("[SCIENCE] ${config.name}", fields)

                return controlResult.value
            } else {
                return control()
            }
        }
    }

    fun <T : Any?> createExperiment(config: Config) = Experiment<T>(config)
}
