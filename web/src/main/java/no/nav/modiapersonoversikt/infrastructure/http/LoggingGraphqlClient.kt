package no.nav.modiapersonoversikt.infrastructure.http

import com.expediagroup.graphql.client.GraphQLClient
import com.expediagroup.graphql.types.GraphQLError
import com.expediagroup.graphql.types.GraphQLResponse
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.util.KtorExperimentalAPI
import no.nav.common.utils.IdUtils
import no.nav.modiapersonoversikt.infrastructure.RestConstants
import no.nav.modiapersonoversikt.infrastructure.TjenestekallLogger
import org.slf4j.LoggerFactory
import java.net.URL

typealias HeadersBuilder = HttpRequestBuilder.() -> Unit

class GraphQLException(override val message: String, val errors: List<GraphQLError>) : RuntimeException(message)
fun <T> GraphQLResponse<T>.assertNoErrors(): GraphQLResponse<T> {
    if (this.errors.isNullOrEmpty()) {
        return this
    } else {
        val errors = this.errors!!
        val message = if (errors.size == 1) errors[0].message else "Flere ukjente feil"
        throw GraphQLException(message, errors)
    }
}

private val mapper = jacksonObjectMapper()
    .registerModule(JavaTimeModule())

@KtorExperimentalAPI
class LoggingGraphqlClient(
    private val name: String,
    url: URL
) : GraphQLClient<CIOEngineConfig>(url, CIO, mapper, {}) {
    private val log = LoggerFactory.getLogger(LoggingGraphqlClient::class.java)

    override suspend fun <T> execute(
        query: String,
        operationName: String?,
        variables: Any?,
        resultType: Class<T>,
        requestBuilder: HeadersBuilder
    ): GraphQLResponse<T> {
        val callId = getCallId()
        val requestId = IdUtils.generateId()
        return try {
            val mappedRequestBuilder: HeadersBuilder = {
                requestBuilder.invoke(this)
                header(RestConstants.NAV_CALL_ID_HEADER, callId)
                header("X-Correlation-ID", callId)
            }
            TjenestekallLogger.info(
                "$name-request: $callId ($requestId)",
                mapOf(
                    "operationName" to operationName,
                    "variables" to variables
                )
            )

            val timer: Long = System.currentTimeMillis()
            val response = super.execute(query, operationName, variables, resultType, mappedRequestBuilder)

            val tjenestekallFelt = mapOf(
                "data" to response.data,
                "errors" to response.errors,
                "extensions" to response.extensions,
                "time" to timer.measure()
            )

            if (response.errors.isNullOrEmpty()) {
                TjenestekallLogger.info("$name-response: $callId ($requestId)", tjenestekallFelt)
            } else {
                TjenestekallLogger.error("$name-response: $callId ($requestId)", tjenestekallFelt)
            }

            response
        } catch (exception: Exception) {
            log.error("Feilet ved oppslag mot $name (ID: $callId)", exception)
            TjenestekallLogger.error("$name-response: $callId ($requestId)", mapOf("exception" to exception))
            val error = GraphQLError("Feilet ved oppslag mot $name (ID: $callId)")
            GraphQLResponse(errors = listOf(error))
        }
    }
    private inline fun Long.measure(): Long = System.currentTimeMillis() - this
}
