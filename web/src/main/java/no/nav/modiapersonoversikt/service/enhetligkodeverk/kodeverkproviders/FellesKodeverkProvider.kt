package no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders

import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.legacy.api.domain.kodeverk.generated.apis.KodeverkApi
import no.nav.modiapersonoversikt.legacy.api.domain.kodeverk.generated.models.GetKodeverkKoderBetydningerResponseDTO
import no.nav.modiapersonoversikt.legacy.api.utils.RestConstants
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk

object FellesKodeverk {

    class Provider(
        private val fellesKodeverk: KodeverkApi = createKodeverkApi()
    ) : EnhetligKodeverk.KodeverkProvider<String, String> {

        override fun hentKodeverk(kodeverkNavn: String): EnhetligKodeverk.Kodeverk<String, String> {
            val respons = fellesKodeverk.betydningUsingGET(
                navCallId = getCallId(),
                navConsumerId = RestConstants.MODIABRUKERDIALOG_SYSTEM_USER,
                kodeverksnavn = kodeverkNavn,
                spraak = listOf("nb")
            )
            return EnhetligKodeverk.Kodeverk(kodeverkNavn, parseTilKodeverk(respons))
        }
    }

    internal fun parseTilKodeverk(respons: GetKodeverkKoderBetydningerResponseDTO): Map<String, String> {
        return respons.betydninger.mapValues { entry ->
            entry.value.first().beskrivelser["nb"]?.term ?: entry.key
        }
    }

    internal fun createKodeverkApi(): KodeverkApi {
        val url = EnvironmentUtils.getRequiredProperty("FELLES_KODEVERK_URL")
        val client = RestClient.baseClient().newBuilder()
            .addInterceptor(XCorrelationIdInterceptor())
            .addInterceptor(
                LoggingInterceptor("Felleskodeverk") { request ->
                    requireNotNull(request.header("X-Correlation-ID")) {
                        "Kall uten \"X-Correlation-ID\" er ikke lov"
                    }
                }
            )
            .build()

        return KodeverkApi(url, client)
    }
}
