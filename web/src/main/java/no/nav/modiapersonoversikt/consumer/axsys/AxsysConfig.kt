package no.nav.modiapersonoversikt.consumer.axsys

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.common.client.axsys.AxsysClient
import no.nav.common.client.axsys.AxsysEnhet
import no.nav.common.client.axsys.AxsysV2ClientImpl
import no.nav.common.client.axsys.CachedAxsysClient
import no.nav.common.rest.client.RestClient
import no.nav.common.sts.ServiceToServiceTokenProvider
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.NavIdent
import no.nav.common.utils.EnvironmentUtils
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
open class AxsysConfig {
    val url: String = EnvironmentUtils.getRequiredProperty("AXSYS_URL")
    val httpClient: OkHttpClient = RestClient.baseClient()
    private val enhetCache: Cache<EnhetId, MutableList<NavIdent>> = Caffeine.newBuilder()
        .expireAfterWrite(24, TimeUnit.HOURS)
        .maximumSize(500)
        .build()
    private val tilgangCache: Cache<NavIdent, MutableList<AxsysEnhet>> = Caffeine.newBuilder()
        .expireAfterWrite(24, TimeUnit.HOURS)
        .maximumSize(10_000)
        .build()

    @Autowired
    var tokenProvider: ServiceToServiceTokenProvider

    @Bean
    open fun axsys(): AxsysClient {
        val tokenSupplier = {
            tokenProvider
                .getServiceToken(
                    "axsys",
                    "org",
                    EnvironmentUtils
                        .getRequiredProperty("AXSYS_CLUSTER")
                )
        }

        return CachedAxsysClient(AxsysV2ClientImpl(url, tokenSupplier, httpClient), tilgangCache, enhetCache)
    }
}
