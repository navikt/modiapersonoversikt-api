package no.nav.modiapersonoversikt.infrastructure.http

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.common.log.MDCConstants
import no.nav.modiapersonoversikt.legacy.api.utils.TjenestekallLogger
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.*

object OkHttpUtils {
    val objectMapper = jacksonObjectMapper()
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
                    .withCreatorVisibility(Visibility.NONE)
            )
        }
    object MediaTypes {
        val JSON: MediaType = requireNotNull(MediaType.parse("application/json; charset=utf-8"))
    }
}

class LoggingInterceptor(val name: String, val callIdExtractor: (Request) -> String) : Interceptor {
    private val log = LoggerFactory.getLogger(LoggingInterceptor::class.java)
    private fun Request.peekContent(): String? {
        val copy = this.newBuilder().build()
        val buffer = Buffer()
        copy.body()?.writeTo(buffer)

        return buffer.readUtf8()
    }

    private fun Response.peekContent(): String? {
        return this.peekBody(Long.MAX_VALUE).string()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val callId = callIdExtractor(request)
        val requestBody = request.peekContent()

        TjenestekallLogger.info(
            "$name-request: $callId",
            mapOf(
                "url" to request.url().toString(),
                "body" to requestBody
            )
        )

        val response: Response = runCatching { chain.proceed(request) }
            .onFailure { exception ->
                log.error("$name-response-error (ID: $callId)", exception)
                TjenestekallLogger.error(
                    "$name-response-error: $callId",
                    mapOf(
                        "exception" to exception
                    )
                )
            }
            .getOrThrow()

        val responseBody = response.peekContent()

        if (response.code() in 200..299) {
            TjenestekallLogger.info(
                "$name-response: $callId",
                mapOf(
                    "status" to "${response.code()} ${response.message()}",
                    "body" to responseBody
                )
            )
        } else {
            TjenestekallLogger.error(
                "$name-response-error: $callId",
                mapOf(
                    "status" to "${response.code()} ${response.message()}",
                    "request" to request,
                    "body" to responseBody
                )
            )
        }
        return response
    }
}

sealed class HeadersInterceptor(val headersProvider: () -> Map<String, String>) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request()
            .newBuilder()
        headersProvider()
            .forEach { (name, value) -> builder.addHeader(name, value) }

        return chain.proceed(builder.build())
    }
}
class XCorrelationIdInterceptor() : HeadersInterceptor({
    mapOf("X-Correlation-ID" to (MDC.get(MDCConstants.MDC_CALL_ID) ?: UUID.randomUUID().toString()))
})
class AuthorizationInterceptor(val tokenProvider: () -> String) : HeadersInterceptor({
    mapOf("Authorization" to "Bearer ${tokenProvider()}")
})
