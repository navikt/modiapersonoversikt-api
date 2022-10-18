package no.nav.modiapersonoversikt.consumer.axsys

import no.nav.common.client.axsys.AxsysClient
import no.nav.common.client.axsys.AxsysV2ClientImpl
import no.nav.common.client.axsys.CachedAxsysClient
import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.createMachineToMachineToken
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AxsysConfig {
    private val url: String = EnvironmentUtils.getRequiredProperty("AXSYS_URL")
    private val httpClient: OkHttpClient = RestClient.baseClient()
    companion object {
        val downstreamApi = DownstreamApi.parse(EnvironmentUtils.getRequiredProperty("AXSYS_SCOPE"))
    }

    @Autowired
    lateinit var tokenProvider: MachineToMachineTokenClient

    @Bean
    open fun axsys(): AxsysClient {
        val tokenSupplier = {
            tokenProvider.createMachineToMachineToken(downstreamApi)
        }

        return CachedAxsysClient(AxsysV2ClientImpl(url, tokenSupplier, httpClient))
    }
}
