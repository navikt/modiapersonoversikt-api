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
        val reporter: Reporter = defaultReporter,
        val logAndCompareValues: Boolean = true
    )

    data class TimedValue<T>(val value: T, val time: Long)
    fun <T> measureTimeInMillies(block: () -> T): TimedValue<T> {
        val startTime = System.currentTimeMillis()
        val value = block()
        val time = System.currentTimeMillis() - startTime
        return TimedValue(value, time)
    }

    data class Result<T>(
        val experimentRun: Boolean,
        val controlValue: T,
        val experimentValue: Any? = null,
        val experimentException: Throwable? = null
    )
    data class WithFields<T>(val data: T, val fields: Map<String, Any?>)

    class Experiment<T> internal constructor(private val config: Config) {
        fun runIntrospected(
            control: () -> WithFields<T>,
            experiment: () -> WithFields<Any?>
        ): Result<T> {
            if (forceExperiment.get() == true || Random.nextDouble() < config.experimentRate) {
                val fields = mutableMapOf<String, Any?>()
                val controlResult = measureTimeInMillies(control)
                val experimentResult = runCatching {
                    measureTimeInMillies(experiment)
                }

                if (experimentResult.isFailure) {
                    fields["ok"] = false
                    if (config.logAndCompareValues) {
                        fields["control"] = controlResult.value.data
                    }
                    fields["controlTime"] = controlResult.time
                    fields["exception"] = experimentResult.exceptionOrNull()
                    fields.putAll(controlResult.value.fields)
                } else {
                    if (config.logAndCompareValues) {
                        val controlValue = controlResult.value.data
                        val experimentValue = experimentResult.getOrThrow().value.data
                        val (ok, controlJson, experimentJson) = compareAndSerialize(controlValue, experimentValue)
                        fields["ok"] = ok
                        fields["control"] = controlJson
                        fields["experiment"] = experimentJson
                        fields.putAll(controlResult.value.fields)
                        fields.putAll(experimentResult.getOrThrow().value.fields)
                    } else {
                        fields["ok"] = true
                    }
                    fields["controlTime"] = controlResult.time
                    fields["experimentTime"] = experimentResult.getOrThrow().time
                }

                config.reporter("[SCIENCE] ${config.name}", fields)

                return Result(
                    experimentRun = true,
                    controlValue = controlResult.value.data,
                    experimentValue = experimentResult.getOrNull()?.value?.data,
                    experimentException = experimentResult.exceptionOrNull()
                )
            } else {
                return Result(
                    experimentRun = false,
                    controlValue = control().data
                )
            }
        }

        fun runWithExtraFields(control: () -> WithFields<T>, experiment: () -> WithFields<Any?>): T =
            runIntrospected(control, experiment).controlValue

        fun run(control: () -> T, experiment: () -> Any?): T =
            runWithExtraFields(
                control = { WithFields(control(), emptyMap()) },
                experiment = { WithFields(experiment(), emptyMap()) }
            )
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
