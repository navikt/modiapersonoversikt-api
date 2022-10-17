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
    private val dittnavEventHandlerUrl: String = getRequiredProperty("DITTNAV_EVENTER_HANDLER_URL")
    private val downstreamApi: DownstreamApi = DownstreamApi.parse(getRequiredProperty("DITTNAV_EVENTER_HANDLER_SCOPE"))

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
                    baseUrl = dittnavEventerModiaUrl,
                    authInterceptor = HeadersInterceptor {
                        mapOf(
                            "Cookie" to "ID_token=${AuthContextUtils.requireToken()}"
                        )
                    }
                ),
                azureAdClient = BrukernotifikasjonClient(
                    baseUrl = dittnavEventHandlerUrl,
                    authInterceptor = HeadersInterceptor {
                        val azureAdToken = AuthContextUtils.requireAzureAdUserToken()
                        val oboToken = oboTokenProvider.exchangeOnBehalfOfToken(downstreamApi, azureAdToken)
                        mapOf("Authorization" to "Bearer $oboToken")
                    }
                )
            )
        )
}
