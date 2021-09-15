package no.nav.modiapersonoversikt.service.enhetligkodeverk

import no.nav.common.log.MDCConstants
import no.nav.common.rest.client.RestClient
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.legacy.api.domain.kodeverk.generated.apis.KodeverkApi
import no.nav.modiapersonoversikt.legacy.api.domain.kodeverk.generated.models.GetKodeverkKoderBetydningerResponseDTO
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.TemagruppeDTO
import no.nav.modiapersonoversikt.legacy.api.utils.RestConstants
import org.slf4j.MDC
import java.util.*
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.apis.KodeverkApi as KodeverkApiSf

class KodeverkProviders(
    private val fellesKodeverk: KodeverkApi,
    private val sfHenvendelseKodeverk: KodeverkApiSf
) {
    fun fraFellesKodeverk(kodeverkNavn: String): EnhetligKodeverk.Kodeverk {
        val respons = fellesKodeverk.betydningUsingGET(
            navCallId = MDC.get(MDCConstants.MDC_CALL_ID) ?: UUID.randomUUID().toString(),
            navConsumerId = RestConstants.MODIABRUKERDIALOG_SYSTEM_USER,
            kodeverksnavn = kodeverkNavn,
            spraak = listOf("nb")
        )
        return EnhetligKodeverk.Kodeverk(kodeverkNavn, parseTilKodeverk(respons))
    }

    fun fraSfHenvendelseKodeverk(): EnhetligKodeverk.Kodeverk {
        val respons = sfHenvendelseKodeverk.henvendelseKodeverkTemagrupperGet(
            MDC.get(MDCConstants.MDC_CALL_ID) ?: UUID.randomUUID().toString()
        )
        return EnhetligKodeverk.Kodeverk("SF_TEMAGRUPPE", parseTilKodeverk(respons))
    }

    private fun parseTilKodeverk(respons: List<TemagruppeDTO>): Map<String, String> {
        return respons.associate { (navn, kode) ->
            kode to navn
        }
    }

    private fun parseTilKodeverk(respons: GetKodeverkKoderBetydningerResponseDTO): Map<String, String> {
        return respons.betydninger.mapValues { entry ->
            entry.value.first().beskrivelser["nb"]?.term ?: entry.key
        }
    }

    companion object {

        fun createFelleskodeverkApi(): KodeverkApi {
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

        fun createSfHenvendelseKodeverkApi(systemUserTokenProvider: SystemUserTokenProvider): KodeverkApiSf {
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

            return KodeverkApiSf(url, client)
        }
    }
}
