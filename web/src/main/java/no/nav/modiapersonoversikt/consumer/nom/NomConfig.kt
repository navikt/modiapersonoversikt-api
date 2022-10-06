package no.nav.modiapersonoversikt.consumer.nom

import no.nav.common.client.nom.CachedNomClient
import no.nav.common.client.nom.NomClient
import no.nav.common.client.nom.NomClientImpl
import no.nav.common.client.nom.VeilederNavn
import no.nav.common.health.HealthCheckResult
import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.types.identer.NavIdent
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.createMachineToMachineToken
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class NomConfig {
    private val scope = DownstreamApi(
        application = "nom-api",
        namespace = "nom",
        cluster = "prod-gcp"
    )
    private val url: String = EnvironmentUtils.getRequiredProperty("NOM_URL")
    private val httpClient: OkHttpClient = RestClient.baseClient()

    @Autowired
    lateinit var tokenProvider: MachineToMachineTokenClient

    @Bean
    open fun nom(): NomClient {
        if (EnvironmentUtils.isDevelopment().orElse(false)) {
            return DevNomClient()
        }
        val tokenSupplier = { tokenProvider.createMachineToMachineToken(scope) }
        return CachedNomClient(NomClientImpl(url, tokenSupplier, httpClient))
    }
}

private class DevNomClient : NomClient {
    override fun checkHealth(): HealthCheckResult = HealthCheckResult.healthy()

    override fun finnNavn(navIdent: NavIdent): VeilederNavn {
        return lagVeilederNavn(navIdent)
    }

    override fun finnNavn(identer: MutableList<NavIdent>): List<VeilederNavn> {
        return identer.map(::lagVeilederNavn)
    }

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
