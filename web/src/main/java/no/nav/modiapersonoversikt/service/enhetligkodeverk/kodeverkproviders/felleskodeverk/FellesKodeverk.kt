package no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.felleskodeverk

import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.config.AppConstants
import no.nav.modiapersonoversikt.consumer.kodeverk.generated.apis.KodeverkApi
import no.nav.modiapersonoversikt.consumer.kodeverk.generated.models.GetKodeverkKoderBetydningerResponseDTO
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

object FellesKodeverk {
    class Provider(
        private val fellesKodeverk: KodeverkApi = createKodeverkApi(),
    ) : EnhetligKodeverk.KodeverkProvider<String, String> {
        override fun hentKodeverk(kodeverkNavn: String): EnhetligKodeverk.Kodeverk<String, String> {
            val respons =
                fellesKodeverk.betydningUsingGET(
                    navCallId = getCallId(),
                    navConsumerId = AppConstants.APP_NAME,
                    kodeverksnavn = kodeverkNavn,
                    spraak = listOf("nb"),
                ) ?: throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Feil ved henting av kodverk.",
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
        val client =
            RestClient.baseClient().newBuilder()
                .addInterceptor(XCorrelationIdInterceptor())
                .addInterceptor(
                    LoggingInterceptor("Felleskodeverk") { request ->
                        requireNotNull(request.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    },
                )
                .build()

        return KodeverkApi(url, client)
    }
}
