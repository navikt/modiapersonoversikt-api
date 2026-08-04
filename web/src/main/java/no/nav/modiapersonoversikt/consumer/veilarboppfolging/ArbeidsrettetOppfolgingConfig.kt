package no.nav.modiapersonoversikt.consumer.veilarboppfolging

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.infrastructure.http.LoggingGraphqlClient
import no.nav.modiapersonoversikt.infrastructure.ping.ConsumerPingable
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.bindTo
import no.nav.personoversikt.common.logging.TjenestekallLogger
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.net.URI

@Configuration
@EnableCaching
@Profile("!local")
open class ArbeidsrettetOppfolgingConfig {
    private val url: String = getRequiredProperty("VEILARBOPPFOLGINGAPI_URL")
    private val downstreamApi = DownstreamApi.parse(getRequiredProperty("VEILARBOPPFOLGINGAPI_SCOPE"))

    @Bean
    open fun oppfolgingsApi(
        ansattService: AnsattService,
        onBehalfOfTokenClient: OnBehalfOfTokenClient,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
        tjenestekallLogger: TjenestekallLogger,
    ): ArbeidsrettetOppfolging.Service {
        val gqlHttpClient =
            HttpClient(engineFactory = OkHttp) {
                engine {
                    addInterceptor(
                        tjenestekallLoggingInterceptorFactory("veilarboppfolging-gql") { request ->
                            requireNotNull(request.header("X-Correlation-ID")) {
                                "Kall uten \"X-Correlation-ID\" er ikke lov"
                            }
                        },
                    )
                }
            }

        val graphQLClient =
            LoggingGraphqlClient(
                "Veilarboppfolging",
                URI("$url/graphql").toURL(),
                gqlHttpClient,
                tjenestekallLogger,
            )

        return ArbeidsrettetOppfolgingServiceImpl(
            apiUrl = url,
            ansattService = ansattService,
            graphQLClient = graphQLClient,
            oboTokenClient = onBehalfOfTokenClient.bindTo(downstreamApi),
        )
    }

    @Bean
    open fun oppfolgingsApiPing(service: ArbeidsrettetOppfolging.Service): Pingable =
        ConsumerPingable(
            "OppfolgingsInfoApi",
            service::ping,
        )
}
