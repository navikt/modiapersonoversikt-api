package no.nav.modiapersonoversikt.consumer.brukernotifikasjon

import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.types.identer.Fnr
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.HeadersInterceptor
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.exchangeOnBehalfOfToken
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class BrukernotifikasjonConfig {
    private val dittnavEventerModiaUrl: String = getRequiredProperty("DITTNAV_EVENTER_MODIA_URL")
    private val tmsEventApiUrl: String = getRequiredProperty("TMS_EVENT_API_URL")
    private val tmsEventApiApi: DownstreamApi = DownstreamApi.parse(getRequiredProperty("TMS_EVENT_API_SCOPE"))

    private class TokenawareSwitcher(
        val openAmClient: Brukernotifikasjon.Client,
        val azureAdClient: Brukernotifikasjon.Client,
    ) : Brukernotifikasjon.Client {
        override fun hentBrukernotifikasjoner(type: Brukernotifikasjon.Type, fnr: Fnr): List<Brukernotifikasjon.Event> {
            return if (AuthContextUtils.azureAdUserToken() != null) {
                azureAdClient.hentBrukernotifikasjoner(type, fnr)
            } else {
                openAmClient.hentBrukernotifikasjoner(type, fnr)
            }
        }
    }

    @Bean
    open fun brukernotifikasjonService(oboTokenProvider: OnBehalfOfTokenClient): Brukernotifikasjon.Service =
        BrukernotifikasjonService(
            TokenawareSwitcher(
                openAmClient = BrukernotifikasjonClient(
                    baseUrl = "$dittnavEventerModiaUrl/fetch",
                    authInterceptor = HeadersInterceptor {
                        mapOf(
                            "Cookie" to "ID_token=${AuthContextUtils.requireToken()}"
                        )
                    }
                ),
                azureAdClient = BrukernotifikasjonClient(
                    baseUrl = tmsEventApiUrl,
                    authInterceptor = HeadersInterceptor {
                        val azureAdToken = AuthContextUtils.requireAzureAdUserToken()
                        val oboToken = oboTokenProvider.exchangeOnBehalfOfToken(tmsEventApiApi, azureAdToken)
                        mapOf("Authorization" to "Bearer $oboToken")
                    }
                )
            )
        )
}
