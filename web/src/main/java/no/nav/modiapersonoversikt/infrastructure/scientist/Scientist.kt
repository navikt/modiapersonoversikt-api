package no.nav.modiapersonoversikt.infrastructure.scientist

import com.fasterxml.jackson.databind.JsonNode
import no.nav.modiapersonoversikt.config.JacksonConfig
import no.nav.modiapersonoversikt.infrastructure.scientist.Scientist.UtilityClasses.Try
import no.nav.modiapersonoversikt.infrastructure.scientist.Scientist.UtilityClasses.measureTimeInMillies
import no.nav.modiapersonoversikt.legacy.api.utils.TjenestekallLogger
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.utils.ConcurrencyUtils
import kotlin.random.Random

typealias Reporter = (header: String, fields: Map<String, Any?>) -> Unit

object Scientist {

    internal val forceExperiment: ThreadLocal<Boolean?> = ThreadLocal()
    private val defaultReporter: Reporter = { header, fields -> TjenestekallLogger.info(header, fields) }

    private object UtilityClasses {
        data class TimedValue<T>(val value: T, val time: Long)
        fun <T> measureTimeInMillies(block: () -> T): TimedValue<T> {
            val startTime = System.currentTimeMillis()
            val value = block()
            val time = System.currentTimeMillis() - startTime
            return TimedValue(value, time)
        }

        class Try<out T> private constructor(private val value: Any?) {
            val isFailure: Boolean = value is Failure

            companion object {
                fun <T> success(value: T): Try<T> = Try(value)
                fun <T> failure(exception: Throwable): Try<T> = Try(Failure(exception))
                fun <T> of(block: () -> T): Try<T> = try {
                    success(block())
                } catch (e: Throwable) {
                    failure(e)
                }
            }

            fun getOrNull(): T? = when {
                isFailure -> null
                else -> value as T
            }
            fun exceptionOrNull(): Throwable? = when (value) {
                is Failure -> value.exception
                else -> null
            }
            fun getOrThrow(): T = when (value) {
                is Failure -> throw value.exception
                else -> value as T
            }

            data class Failure(val exception: Throwable)
        }
    }

    interface ExperimentRate {
        fun shouldRunExperiment(): Boolean
    }
    class FixedValueRate(private val rate: Double) : ExperimentRate {
        override fun shouldRunExperiment(): Boolean {
            return Random.nextDouble() < rate
        }
    }
    class UnleashRate(private val unleash: UnleashService, val feature: Feature) : ExperimentRate {
        override fun shouldRunExperiment(): Boolean {
            return unleash.isEnabled(feature)
        }
    }
    data class Config(
        val name: String,
        val experimentRate: ExperimentRate,
        val reporter: Reporter = defaultReporter,
        val logAndCompareValues: Boolean = true
    )

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
            experiment: () -> WithFields<Any?>,
            dataFields: ((T, Any?) -> Map<String, Any?>)?
        ): Result<T> {
            if (forceExperiment.get() == true || config.experimentRate.shouldRunExperiment()) {
                val fields = mutableMapOf<String, Any?>()
                val (controlResult, experimentResult) = ConcurrencyUtils.inParallel(
                    { measureTimeInMillies(control) },
                    { Try.of { measureTimeInMillies(experiment) } }
                )

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
                    } else {
                        fields["ok"] = true
                    }
                    fields["controlTime"] = controlResult.time
                    fields["experimentTime"] = experimentResult.getOrThrow().time
                    fields.putAll(controlResult.value.fields)
                    fields.putAll(experimentResult.getOrThrow().value.fields)
                    if (dataFields != null) {
                        fields.putAll(dataFields(controlResult.value.data, experimentResult.getOrThrow().value.data))
                    }
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

        fun runWithExtraFields(
            control: () -> WithFields<T>,
            experiment: () -> WithFields<Any?>,
            dataFields: ((T, Any?) -> Map<String, Any?>)? = null
        ): T =
            runIntrospected(control, experiment, dataFields).controlValue

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
