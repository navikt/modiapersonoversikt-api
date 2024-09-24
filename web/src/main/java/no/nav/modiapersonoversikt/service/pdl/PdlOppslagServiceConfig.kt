package no.nav.modiapersonoversikt.service.pdl

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.http.LoggingGraphqlClient
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.bindTo
import no.nav.personoversikt.common.logging.TjenestekallLogger
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URL

@Configuration
@EnableCaching
open class PdlOppslagServiceConfig {
    private val pdlApiUrl: URL = EnvironmentUtils.getRequiredProperty("PDL_API_URL").let(::URL)

    @Bean
    open fun pdlOppslagService(
        machineToMachineTokenClient: MachineToMachineTokenClient,
        oboTokenClient: OnBehalfOfTokenClient,
        unleashService: UnleashService,
        tjenestekallLogger: TjenestekallLogger,
    ): PdlOppslagService {
        val gqlHttpClient =
            HttpClient(engineFactory = OkHttp) {
                engine {
                    config {
                    }
                    addInterceptor(
                        LoggingInterceptor(unleashService, "PDL", tjenestekallLogger) { request ->
                            requireNotNull(request.header("X-Correlation-ID")) {
                                "Kall uten \"X-Correlation-ID\" er ikke lov"
                            }
                        },
                    )
                }
            }

        val gqlClient = LoggingGraphqlClient("PDL", pdlApiUrl, gqlHttpClient, tjenestekallLogger)

        return PdlOppslagServiceImpl(
            gqlClient,
            machineToMachineTokenClient.bindTo(downstreamApi),
            oboTokenClient.bindTo(downstreamApi),
        )
    }

    companion object {
        val downstreamApi = DownstreamApi.parse(EnvironmentUtils.getRequiredProperty("PDL_SCOPE"))
    }
}
