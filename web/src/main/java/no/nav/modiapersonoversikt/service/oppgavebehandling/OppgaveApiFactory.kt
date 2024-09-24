package no.nav.modiapersonoversikt.service.oppgavebehandling

import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.consumer.oppgave.generated.apis.OppgaveApi
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.utils.DownstreamApi

object OppgaveApiFactory {
    val url: String = EnvironmentUtils.getRequiredProperty("OPPGAVE_BASEURL")
    val downstreamApi = DownstreamApi.parse(EnvironmentUtils.getRequiredProperty("OPPGAVE_SCOPE"))

    fun createClient(
        tokenProvider: () -> String,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
    ): OppgaveApi {
        val client =
            RestClient
                .baseClient()
                .newBuilder()
                .addInterceptor(
                    tjenestekallLoggingInterceptorFactory("Oppgave") { request ->
                        requireNotNull(request.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    },
                ).addInterceptor(AuthorizationInterceptor(tokenProvider))
                .build()
        return OppgaveApi(url, client)
    }
}
