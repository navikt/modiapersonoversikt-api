package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.rest

import no.nav.common.log.MDCConstants
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.apis.OppgaveApi
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.TjenestekallLogger
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.slf4j.MDC

private class LoggingInterceptor(val callIdExtractor: (Request) -> String) : Interceptor {
    fun Request.peekContent(): String? {
        val copy = this.newBuilder().build()
        val buffer = Buffer()
        copy.body()?.writeTo(buffer)

        return buffer.readUtf8()
    }

    fun Response.peekContent(): String? {
        return this.peekBody(Long.MAX_VALUE).string()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val callId = callIdExtractor(request)
        val requestBody = request.peekContent()

        TjenestekallLogger.info(
            "Oppgaver-request: $callId", mapOf(
                "url" to request.url().toString(),
                "body" to requestBody,
                "callId" to MDC.get(MDCConstants.MDC_CALL_ID)
            )
        )

        val response: Response = runCatching { chain.proceed(request) }
            .onFailure { exception ->
                TjenestekallLogger.error(
                    "Oppgave-response-error: $callId", mapOf(
                        "exception" to exception
                    )
                )
            }
            .getOrThrow()

        val responseBody = response.peekContent()

        if (response.code() in 200..299) {
            TjenestekallLogger.info(
                "Oppgave-response: $callId", mapOf(
                    "status" to "${response.code()} ${response.message()}",
                    "body" to responseBody
                )
            )
        } else {
            TjenestekallLogger.error(
                "Oppgave-response-error: $callId", mapOf(
                    "status" to "${response.code()} ${response.message()}",
                    "request" to request,
                    "body" to responseBody
                )
            )
        }
        return response
    }
}

typealias TokenProvider = () -> String
private class AuthInterceptor(val tokenProvider: TokenProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenProvider()
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}

object OppgaveApiFactory {
    fun createClient(tokenProvider: TokenProvider): OppgaveApi {
        val url = EnvironmentUtils.getRequiredProperty("OPPGAVE_BASEURL")
        val client = OkHttpClient.Builder()
            .addInterceptor(LoggingInterceptor { request ->
                requireNotNull(request.header("X-Correlation-ID")) {
                    "Kall uten \"X-Correlation-ID\" er ikke lov"
                }
            })
            .addInterceptor(AuthInterceptor(tokenProvider))
            .build()
        return OppgaveApi(url, client)
    }
}
