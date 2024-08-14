package no.nav.modiapersonoversikt.infrastructure.http

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.types.GraphQLClientError
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import no.nav.common.utils.IdUtils
import no.nav.modiapersonoversikt.infrastructure.RestConstants
import no.nav.personoversikt.common.logging.TjenestekallLogg
import org.slf4j.LoggerFactory
import java.net.URL

typealias HeadersBuilder = HttpRequestBuilder.() -> Unit

class GraphQLException(
    override val message: String,
    val errors: List<GraphQLClientError>,
) : RuntimeException(message)

fun <T> GraphQLClientResponse<T>.assertNoErrors(): GraphQLClientResponse<T> {
    if (this.errors.isNullOrEmpty()) {
        return this
    } else {
        val errors = this.errors!!
        val message = if (errors.size == 1) errors[0].message else "Flere ukjente feil"
        throw GraphQLException(message, errors)
    }
}

private val mapper =
    jacksonObjectMapper()
        .registerModule(JavaTimeModule())

class LoggingGraphqlClient(
    private val name: String,
    url: URL,
    httpClient: HttpClient,
) : GraphQLKtorClient(url, httpClient) {
    private val log = LoggerFactory.getLogger(LoggingGraphqlClient::class.java)

    override suspend fun <T : Any> execute(
        request: GraphQLClientRequest<T>,
        requestCustomizer: HttpRequestBuilder.() -> Unit,
    ): GraphQLClientResponse<T> {
        val callId = getCallId()
        val requestId = IdUtils.generateId()
        return try {
            val mappedRequestBuilder: HeadersBuilder = {
                requestCustomizer.invoke(this)
                header(RestConstants.NAV_CALL_ID_HEADER, callId)
                header("X-Correlation-ID", callId)
            }
            TjenestekallLogg.info(
                "$name-request: $callId ($requestId)",
                mapOf(
                    "operationName" to request.operationName,
                    "variables" to request.variables,
                ),
            )

            val timer: Long = System.currentTimeMillis()
            val response = super.execute(request, mappedRequestBuilder)

            val tjenestekallFelt =
                mapOf(
                    "data" to response.data,
                    "errors" to response.errors,
                    "extensions" to response.extensions,
                    "time" to timer.measure(),
                )

            if (response.errors.isNullOrEmpty()) {
                TjenestekallLogg.info("$name-response: $callId ($requestId)", tjenestekallFelt)
            } else {
                TjenestekallLogg.error("$name-response: $callId ($requestId)", tjenestekallFelt)
            }

            response
        } catch (exception: Exception) {
            log.error("Feilet ved oppslag mot $name (ID: $callId)", exception)
            TjenestekallLogg.error(
                header = "$name-response: $callId ($requestId)",
                fields = mapOf("exception" to exception.message),
                throwable = exception,
            )
            val error = GenericGraphQlError("Feilet ved oppslag mot $name (ID: $callId)")
            GenericGraphQlResponse(errors = listOf(error), data = null)
        }
    }

    private inline fun Long.measure(): Long = System.currentTimeMillis() - this
}

data class GenericGraphQlResponse<T>(
    override val errors: List<GraphQLClientError>? = null,
    override val data: T? = null,
) : GraphQLClientResponse<T>

data class GenericGraphQlError(
    override val message: String,
) : GraphQLClientError
