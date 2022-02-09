package no.nav.modiapersonoversikt.consumer.nom

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.common.client.nom.CachedNomClient
import no.nav.common.client.nom.NomClient
import no.nav.common.client.nom.NomClientImpl
import no.nav.common.client.nom.VeilederNavn
import no.nav.common.health.HealthCheckResult
import no.nav.common.rest.client.RestClient
import no.nav.common.sts.ServiceToServiceTokenProvider
import no.nav.common.types.identer.NavIdent
import no.nav.common.utils.EnvironmentUtils
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
open class NomConfig {
    val url: String = EnvironmentUtils.getRequiredProperty("NOM_URL")
    val httpClient: OkHttpClient = RestClient.baseClient()
    private val veilederCache: Cache<NavIdent, VeilederNavn> = Caffeine.newBuilder()
        .expireAfterWrite(24, TimeUnit.HOURS)
        .maximumSize(10_000)
        .build()

    @Autowired
    var tokenProvider: ServiceToServiceTokenProvider

    @Bean
    open fun nom(): NomClient {
        if (EnvironmentUtils.isDevelopment().orElse(false)) {
            return DevNomClient()
        }
        val tokenSupplier = { tokenProvider.getServiceToken("nom-api", "nom", "prod-gcp") }
        return CachedNomClient(NomClientImpl(url, tokenSupplier, httpClient), veilederCache)
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
