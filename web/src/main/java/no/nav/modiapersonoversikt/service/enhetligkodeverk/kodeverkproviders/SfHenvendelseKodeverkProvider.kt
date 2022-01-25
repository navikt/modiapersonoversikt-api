package no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders

import no.nav.common.rest.client.RestClient
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.apis.KodeverkApi
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.TemagruppeDTO
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk

class SfHenvendelseKodeverkProvider(
    private val sfHenvendelseKodeverk: KodeverkApi
) : EnhetligKodeverk.KodeverkProvider<String, String> {

    override fun hentKodeverk(kodeverkNavn: String): EnhetligKodeverk.Kodeverk<String, String> {
        val respons = sfHenvendelseKodeverk.henvendelseKodeverkTemagrupperGet(
            getCallId()
        )
        return EnhetligKodeverk.Kodeverk(kodeverkNavn, parseTilKodeverk(respons))
    }

    private fun parseTilKodeverk(respons: List<TemagruppeDTO>): Map<String, String> {
        return respons.associate { (navn, kode) ->
            kode to navn
        }
    }

    companion object {
        fun createKodeverkApi(systemUserTokenProvider: SystemUserTokenProvider): KodeverkApi {
            val url = EnvironmentUtils.getRequiredProperty("SF_HENVENDELSE_URL")
            val client = RestClient.baseClient().newBuilder()
                .addInterceptor(
                    LoggingInterceptor("SF-Henvendelse-Kodeverk") { request ->
                        requireNotNull(request.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    }
                )
                .addInterceptor(
                    AuthorizationInterceptor {
                        systemUserTokenProvider.systemUserToken
                    }
                )
                .build()

            return KodeverkApi(url, client)
        }
    }
}
