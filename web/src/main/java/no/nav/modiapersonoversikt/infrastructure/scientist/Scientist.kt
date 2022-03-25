package no.nav.modiapersonoversikt.infrastructure.scientist

import com.fasterxml.jackson.databind.JsonNode
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils.objectMapper
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

    object UtilityClasses {
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

    class Experiment<T> internal constructor(private val config: Config) {
        fun runIntrospected(
            control: () -> T,
            experiment: () -> Any?,
            dataFields: ((T, Try<Any?>) -> Map<String, Any?>)? = null
        ): Result<T> {
            if (forceExperiment.get() == true || config.experimentRate.shouldRunExperiment()) {
                val fields = mutableMapOf<String, Any?>()
                val (controlResult, experimentResult) = ConcurrencyUtils.inParallel(
                    { measureTimeInMillies(control) },
                    { measureTimeInMillies { Try.of(experiment) } }
                )

                if (experimentResult.value.isFailure) {
                    fields["ok"] = false
                    if (config.logAndCompareValues) {
                        fields["control"] = controlResult.value
                    }
                    fields["controlTime"] = controlResult.time
                    fields["experimentTime"] = experimentResult.time
                    fields["exception"] = experimentResult.value.exceptionOrNull()
                } else {
                    if (config.logAndCompareValues) {
                        val controlValue = controlResult.value
                        val experimentValue = experimentResult.value.getOrThrow()
                        val (ok, controlJson, experimentJson) = compareAndSerialize(controlValue, experimentValue)
                        fields["ok"] = ok
                        fields["control"] = controlJson
                        fields["experiment"] = experimentJson
                    } else {
                        fields["ok"] = true
                    }
                    fields["controlTime"] = controlResult.time
                    fields["experimentTime"] = experimentResult.time
                }
                if (dataFields != null) {
                    fields.putAll(dataFields(controlResult.value, experimentResult.value))
                }

                config.reporter("[SCIENCE] ${config.name}", fields)

                return Result(
                    experimentRun = true,
                    controlValue = controlResult.value,
                    experimentValue = experimentResult.value.getOrNull(),
                    experimentException = experimentResult.value.exceptionOrNull()
                )
            } else {
                return Result(
                    experimentRun = false,
                    controlValue = control()
                )
            }
        }

        fun run(
            control: () -> T,
            experiment: () -> Any?,
            dataFields: ((T, Try<Any?>) -> Map<String, Any?>)? = null
        ): T =
            runIntrospected(
                control = control,
                experiment = experiment,
                dataFields = dataFields
            ).controlValue
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
        val json = objectMapper.writeValueAsString(value)
        val tree = objectMapper.readTree(json)
        return Pair(json, tree)
    }
}
