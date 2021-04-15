package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling

import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.apis.OppgaveApi
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.http.AuthorizationInterceptor
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.http.LoggingInterceptor

object OppgaveApiFactory {
    fun createClient(tokenProvider: () -> String): OppgaveApi {
        val url = EnvironmentUtils.getRequiredProperty("OPPGAVE_BASEURL")
        val client = RestClient.baseClient().newBuilder()
            .addInterceptor(
                LoggingInterceptor("Oppgave") { request ->
                    requireNotNull(request.header("X-Correlation-ID")) {
                        "Kall uten \"X-Correlation-ID\" er ikke lov"
                    }
                }
            )
            .addInterceptor(AuthorizationInterceptor(tokenProvider))
            .build()
        return OppgaveApi(url, client)
    }
}
