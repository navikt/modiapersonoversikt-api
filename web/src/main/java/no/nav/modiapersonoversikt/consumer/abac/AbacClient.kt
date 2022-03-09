package no.nav.modiapersonoversikt.consumer.abac

import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.rest.client.RestClient
import no.nav.modiapersonoversikt.infrastructure.http.BasicAuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor.Config
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.infrastructure.types.Pingable
import no.nav.modiapersonoversikt.utils.inRange
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable

val abacLogger: Logger = LoggerFactory.getLogger(AbacClient::class.java)
open class AbacClient(
    username: String,
    password: String,
    private val endpointUrl: String
) : HealthCheck, Pingable {
    class AbacException(message: String) : RuntimeException(message)

    private val client: OkHttpClient = RestClient.baseClient().newBuilder()
        .addInterceptor(BasicAuthorizationInterceptor(username, password))
        .addInterceptor(XCorrelationIdInterceptor())
        .addInterceptor(
            LoggingInterceptor("ABAC", Config(ignoreRequestBody = true)) { request ->
                requireNotNull(request.header("X-Correlation-ID")) {
                    "Kall uten \"X-Correlation-ID\" er ikke lov"
                }
            }
        )
        .build()

    @Cacheable("abacClientCache")
    open fun evaluate(request: AbacRequest): AbacResponse {
        val requestJson = JsonMapper.serialize(request)
        val httpRequest = okhttp3.Request.Builder()
            .url(endpointUrl)
            .post(RequestBody.create(MediaType.parse("application/xacml+json"), requestJson))
            .build()
        val response = client.newCall(httpRequest).execute()

        if (response.code() inRange Pair(500, 600)) {
            abacLogger.warn("ABAC returned: ${response.code()} ${response.message()}")
            throw AbacException("An error has occured calling ABAC: ${response.message()}")
        } else if (response.code() inRange Pair(400, 500)) {
            abacLogger.warn("ABAC returned: ${response.code()} ${response.message()}")
            throw AbacException("An error has occured calling ABAC: ${response.code()}")
        }

        val responseJson = response.body()?.string()!!
        return JsonMapper.deserialize(responseJson, AbacResponse::class.java)
    }

    override fun checkHealth(): HealthCheckResult {
        val request = okhttp3.Request.Builder()
            .url(endpointUrl)
            .build()
        return runCatching { client.newCall(request).execute() }
            .fold(
                onSuccess = { HealthCheckResult.healthy() },
                onFailure = { exception -> HealthCheckResult.unhealthy("Helsesjekk mot Abac feiled", exception) }
            )
    }

    override fun ping() = SelfTestCheck("Abac via $endpointUrl", true, this)
}
