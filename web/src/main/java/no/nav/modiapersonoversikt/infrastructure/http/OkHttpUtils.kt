package no.nav.modiapersonoversikt.infrastructure.http

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.common.log.MDCConstants
import no.nav.common.utils.IdUtils
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.personoversikt.common.logging.TjenestekallLogg
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okio.Buffer
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.*

object OkHttpUtils {
    val objectMapper: ObjectMapper =
        jacksonObjectMapper()
            .registerModule(JodaModule())
            .registerModule(JavaTimeModule())
            .registerModule(Jdk8Module())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .apply {
                setVisibility(
                    serializationConfig
                        .defaultVisibilityChecker
                        .withFieldVisibility(Visibility.ANY)
                        .withGetterVisibility(Visibility.NONE)
                        .withSetterVisibility(Visibility.NONE)
                        .withCreatorVisibility(Visibility.NONE),
                )
            }

    object MediaTypes {
        val JSON: MediaType = requireNotNull("application/json; charset=utf-8".toMediaType())
    }
}

class LoggingInterceptor(
    val unleashService: UnleashService,
    val name: String,
    val callIdExtractor: (Request) -> String,
) : Interceptor {
    private val log = LoggerFactory.getLogger(LoggingInterceptor::class.java)

    private fun Request.peekContent(): String? {
        val logRequestBody = unleashService.isEnabled(Feature.LOG_REQUEST_BODY.propertyKey)
        if (logRequestBody) return "IGNORED"
        val copy = this.newBuilder().build()
        val buffer = Buffer()
        copy.body?.writeTo(buffer)

        return buffer.readUtf8()
    }

    private fun Response.peekContent(): String? {
        val logResponseBody = unleashService.isEnabled(Feature.LOG_RESPONSE_BODY.propertyKey)
        if (!logResponseBody) return "IGNORED"
        return when {
            this.header("Content-Length") == "0" -> "Content-Length: 0, didn't try to peek at body"
            this.code == 204 -> "StatusCode: 204, didn't try to peek at body"
            else -> this.peekBody(Long.MAX_VALUE).string()
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val callId = callIdExtractor(request)
        val requestId = IdUtils.generateId()
        val requestBody = request.peekContent()

        TjenestekallLogg.info(
            "$name-request: $callId ($requestId)",
            mapOf(
                "url" to request.url.toString(),
                "headers" to request.headers.names().joinToString(", "),
                "body" to requestBody,
            ),
        )

        val timer: Long = System.currentTimeMillis()
        val response: Response =
            runCatching { chain.proceed(request) }
                .onFailure { exception ->
                    log.error("$name-response-error (ID: $callId / $requestId)", exception)
                    TjenestekallLogg.error(
                        header = "$name-response-error: $callId ($requestId))",
                        fields =
                            mapOf(
                                "exception" to exception.message,
                                "time" to timer.measure(),
                            ),
                        tags =
                            mapOf(
                                "time" to timer.measure(),
                            ),
                        throwable = exception,
                    )
                }
                .getOrThrow()

        val responseBody = response.peekContent()

        if (response.code in 200..299) {
            TjenestekallLogg.info(
                header = "$name-response: $callId ($requestId)",
                fields =
                    mapOf(
                        "status" to "${response.code} ${response.message}",
                        "time" to timer.measure(),
                        "body" to responseBody,
                    ),
                tags =
                    mapOf(
                        "time" to timer.measure(),
                    ),
            )
        } else {
            TjenestekallLogg.error(
                header = "$name-response-error: $callId ($requestId)",
                fields =
                    mapOf(
                        "status" to "${response.code} ${response.message}",
                        "time" to timer.measure(),
                        "body" to responseBody,
                    ),
                tags =
                    mapOf(
                        "time" to timer.measure(),
                    ),
            )
        }
        return response
    }
}

private inline fun Long.measure(): Long = System.currentTimeMillis() - this

open class HeadersInterceptor(val headersProvider: () -> Map<String, String>) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder =
            chain.request()
                .newBuilder()
        headersProvider()
            .forEach { (name, value) -> builder.addHeader(name, value) }

        return chain.proceed(builder.build())
    }
}

class XCorrelationIdInterceptor : HeadersInterceptor({
    mapOf("X-Correlation-ID" to getCallId())
})

class AuthorizationInterceptor(val tokenProvider: () -> String) : HeadersInterceptor({
    mapOf("Authorization" to "Bearer ${tokenProvider()}")
})

class BasicAuthorizationInterceptor(private val username: String, private val password: String) : HeadersInterceptor({
    mapOf("Authorization" to Credentials.basic(username, password))
})

fun getCallId(): String = MDC.get(MDCConstants.MDC_CALL_ID) ?: UUID.randomUUID().toString()
