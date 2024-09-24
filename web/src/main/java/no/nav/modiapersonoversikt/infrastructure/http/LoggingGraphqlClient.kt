package no.nav.modiapersonoversikt.infrastructure.http

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.types.GraphQLClientError
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import no.nav.common.utils.IdUtils
import no.nav.modiapersonoversikt.infrastructure.RestConstants
import no.nav.personoversikt.common.logging.TjenestekallLogger
import org.slf4j.LoggerFactory
import java.net.URL

typealias HeadersBuilder = HttpRequestBuilder.() -> Unit

class GraphQLException(
    override val message: String,
) : RuntimeException(message)

fun <T> GraphQLClientResponse<T>.assertNoErrors(): GraphQLClientResponse<T> {
    if (this.errors.isNullOrEmpty()) {
        return this
    } else {
        val errors = this.errors!!
        val message = if (errors.size == 1) errors[0].message else "Flere ukjente feil"
        throw GraphQLException(message)
    }
}

class LoggingGraphqlClient(
    private val name: String,
    url: URL,
    httpClient: HttpClient,
    private val tjenestekallLogger: TjenestekallLogger,
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
            tjenestekallLogger.info(
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
                tjenestekallLogger.info("$name-response: $callId ($requestId)", tjenestekallFelt)
            } else {
                tjenestekallLogger.error("$name-response: $callId ($requestId)", tjenestekallFelt)
            }

            response
        } catch (exception: Exception) {
            log.error("Feilet ved oppslag mot $name (ID: $callId)", exception)
            tjenestekallLogger.error(
                header = "$name-response: $callId ($requestId)",
                fields = mapOf("exception" to exception.message),
                throwable = exception,
            )
            val error = GenericGraphQlError("Feilet ved oppslag mot $name (ID: $callId)")
            GenericGraphQlResponse(errors = listOf(error), data = null)
        }
    }

    private fun Long.measure(): Long = System.currentTimeMillis() - this
}

data class GenericGraphQlResponse<T>(
    override val errors: List<GraphQLClientError>? = null,
    override val data: T? = null,
) : GraphQLClientResponse<T>

data class GenericGraphQlError(
    override val message: String,
) : GraphQLClientError
