package no.nav.modiapersonoversikt.consumer.krr

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.createMachineToMachineToken
import no.nav.personoversikt.common.logging.TjenestekallLogger
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
open class KrrConfig {
    @Bean
    @Primary
    open fun krrService(
        machineToMachineTokenClient: MachineToMachineTokenClient,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
        tjenestekallLogger: TjenestekallLogger,
    ): Krr.Service {
        val scope = DownstreamApi.parse(EnvironmentUtils.getRequiredProperty("KRR_SCOPE"))
        val httpClient: OkHttpClient =
            RestClient
                .baseClient()
                .newBuilder()
                .addInterceptor(
                    tjenestekallLoggingInterceptorFactory("digdir-krr-proxy") { request ->
                        requireNotNull(request.header("Nav-Call-Id"))
                    },
                ).addInterceptor(
                    AuthorizationInterceptor {
                        machineToMachineTokenClient.createMachineToMachineToken(scope)
                    },
                ).build()

        return KrrServiceImpl(
            EnvironmentUtils.getRequiredProperty("KRR_REST_URL"),
            httpClient,
            tjenestekallLogger,
        )
    }
}
