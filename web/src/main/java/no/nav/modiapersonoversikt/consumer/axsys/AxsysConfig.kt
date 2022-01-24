package no.nav.modiapersonoversikt.consumer.axsys

import com.github.benmanes.caffeine.cache.Cache
import no.nav.common.client.axsys.AxsysClient
import no.nav.common.client.axsys.AxsysClientImpl
import no.nav.common.client.axsys.AxsysEnhet
import no.nav.common.client.axsys.CachedAxsysClient
import no.nav.common.rest.client.RestClient
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.NavIdent
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AxsysConfig {
    val url: String = ""
    val httpClient: OkHttpClient = RestClient.baseClient()
    private val enhetCache: Cache<EnhetId, MutableList<NavIdent>> = TODO("Lage cache")
    private val tilgangCache: Cache<NavIdent, MutableList<AxsysEnhet>> = TODO("Lage cache")

    @Bean
    open fun axsys(): AxsysClient {
        return CachedAxsysClient(AxsysClientImpl(url, httpClient), tilgangCache, enhetCache)
    }
}
