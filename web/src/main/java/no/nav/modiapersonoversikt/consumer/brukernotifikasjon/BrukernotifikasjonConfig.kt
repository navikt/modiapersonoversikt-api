package no.nav.modiapersonoversikt.consumer.brukernotifikasjon

import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.HeadersInterceptor
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.exchangeOnBehalfOfToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class BrukernotifikasjonConfig {
    private val tmsEventApiUrl: String = getRequiredProperty("TMS_EVENT_API_URL")
    private val tmsEventApiApi: DownstreamApi = DownstreamApi.parse(getRequiredProperty("TMS_EVENT_API_SCOPE"))

    @Autowired
    private lateinit var unleashService: UnleashService

    @Bean
    open fun brukernotifikasjonService(oboTokenProvider: OnBehalfOfTokenClient): Brukernotifikasjon.Service =
        BrukernotifikasjonService(
            BrukernotifikasjonClient(
                baseUrl = tmsEventApiUrl,
                authInterceptor = HeadersInterceptor {
                    val azureAdToken = AuthContextUtils.requireToken()
                    val oboToken = oboTokenProvider.exchangeOnBehalfOfToken(tmsEventApiApi, azureAdToken)
                    mapOf("Authorization" to "Bearer $oboToken")
                }
            ),
            unleashService
        )
}
