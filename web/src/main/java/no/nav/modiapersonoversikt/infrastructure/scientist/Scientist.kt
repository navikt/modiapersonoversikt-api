package no.nav.modiapersonoversikt.infrastructure.scientist

import com.fasterxml.jackson.databind.JsonNode
import no.nav.modiapersonoversikt.config.JacksonConfig
import no.nav.modiapersonoversikt.legacy.api.utils.TjenestekallLogger
import kotlin.random.Random

typealias Reporter = (header: String, fields: Map<String, Any?>) -> Unit
object Scientist {

    internal val forceExperiment: ThreadLocal<Boolean?> = ThreadLocal()
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
            this.startTime = System.currentTimeMillis()
        }

        fun elapsed(): Long {
            try {
                return System.currentTimeMillis() - this.startTime
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

    data class Result<T>(
        val experimentRun: Boolean,
        val controlValue: T,
        val experimentValue: Any? = null,
        val experimentException: Throwable? = null
    )
    class Experiment<T> internal constructor(private val config: Config) {
        private val timer = Timer()
        fun runIntrospected(
            control: () -> T,
            experiment: () -> Any?
        ): Result<T> {
            if (forceExperiment.get() == true || Random.nextDouble() < config.experimentRate) {
                val fields = mutableMapOf<String, Any?>()
                val controlResult = timer.time(control)
                val experimentResult = runCatching {
                    timer.time(experiment)
                }

                if (experimentResult.isFailure) {
                    fields["ok"] = false
                    fields["control"] = controlResult.value
                    fields["controlTime"] = controlResult.time
                    fields["exception"] = experimentResult.exceptionOrNull()
                } else {
                    val controlValue = controlResult.value
                    val experimentValue = experimentResult.getOrThrow().value
                    val (ok, controlJson, experimentJson) = compareAndSerialize(controlValue, experimentValue)
                    fields["ok"] = ok
                    fields["control"] = controlJson
                    fields["controlTime"] = controlResult.time
                    fields["experiment"] = experimentJson
                    fields["experimentTime"] = experimentResult.getOrThrow().time
                }

                config.reporter("[SCIENCE] ${config.name}", fields)

                return Result(
                    experimentRun = true,
                    controlValue = controlResult.value,
                    experimentValue = experimentResult.getOrNull()?.value,
                    experimentException = experimentResult.exceptionOrNull()
                )
            } else {
                return Result(
                    experimentRun = false,
                    controlValue = control()
                )
            }
        }

        fun run(control: () -> T, experiment: () -> Any?): T =
            runIntrospected(control, experiment).controlValue
    }

    fun <T : Any?> createExperiment(config: Config) = Experiment<T>(config)

    private fun compareAndSerialize(controlValue: Any?, experimentValue: Any?): Triple<Boolean, String, String> {
        val (controlJson, controlTree) = process(controlValue)
        val (experimentJson, experimentTree) = process(experimentValue)
        return Triple(
            controlTree == experimentTree,
            controlJson,
            experimentJson
        )
    }

    private fun process(value: Any?): Pair<String, JsonNode> {
        val json = JacksonConfig.mapper.writeValueAsString(value)
        val tree = JacksonConfig.mapper.readTree(json)
        return Pair(json, tree)
    }
}
