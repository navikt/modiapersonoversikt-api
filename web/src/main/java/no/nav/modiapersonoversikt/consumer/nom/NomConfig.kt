package no.nav.modiapersonoversikt.consumer.nom

import no.nav.common.client.nom.NomClient
import no.nav.common.client.nom.NomClientImpl
import no.nav.common.client.nom.VeilederNavn
import no.nav.common.health.HealthCheckResult
import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.types.identer.NavIdent
import no.nav.common.utils.EnvironmentUtils
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.createMachineToMachineToken
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class NomConfig {
    private val scope = DownstreamApi.parse(getRequiredProperty("NOM_SCOPE"))
    private val url: String = getRequiredProperty("NOM_URL")

    @Bean
    open fun nom(
        tokenProvider: MachineToMachineTokenClient,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
    ): NomClient {
        val httpClient: OkHttpClient =
            RestClient
                .baseClient()
                .newBuilder()
                .addInterceptor(
                    tjenestekallLoggingInterceptorFactory("Nom") {
                        // Optimalt sett burde denne hentes fra requesten, men det sendes ikke noe tilsvarende callId til Nom
                        getCallId()
                    },
                ).build()
        if (EnvironmentUtils.isDevelopment().orElse(false)) {
            return DevNomClient()
        }
        val tokenSupplier = { tokenProvider.createMachineToMachineToken(scope) }
        return NullCachingNomClient(NomClientImpl(url, tokenSupplier, httpClient))
    }
}

private class DevNomClient : NomClient {
    override fun checkHealth(): HealthCheckResult = HealthCheckResult.healthy()

    override fun finnNavn(navIdent: NavIdent): VeilederNavn = lagVeilederNavn(navIdent)

    override fun finnNavn(identer: MutableList<NavIdent>): List<VeilederNavn> = identer.map(::lagVeilederNavn)

    private fun lagVeilederNavn(navIdent: NavIdent): VeilederNavn {
        val ident = navIdent.get()
        val identNr = ident.substring(1)
        return VeilederNavn()
            .setNavIdent(navIdent)
            .setFornavn("F_$identNr")
            .setEtternavn("E_$identNr")
            .setVisningsNavn("F_$identNr E_$identNr")
    }
}
