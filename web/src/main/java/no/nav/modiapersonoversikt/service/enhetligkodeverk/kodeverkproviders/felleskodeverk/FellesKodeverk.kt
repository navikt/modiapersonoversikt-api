package no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.felleskodeverk

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.config.AppConstants
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.consumer.kodeverk.generated.apis.KodeverkApi
import no.nav.modiapersonoversikt.consumer.kodeverk.generated.models.GetKodeverkKoderBetydningerResponseDTO
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.createMachineToMachineToken
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

object FellesKodeverk {
    class Provider(
        private val machineToMachineTokenClient: MachineToMachineTokenClient,
        private val tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
        private val fellesKodeverk: KodeverkApi =
            createKodeverkApi(
                machineToMachineTokenClient,
                tjenestekallLoggingInterceptorFactory,
            ),
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

            if (kodeverkNavn == "Arkivtemaer") {
                respons.betydninger.forEach { (temakode, betydninger) ->
                    LOG.info(
                        "Arkivtemaer-diagnostikk temakode={} betydninger={}",
                        temakode,
                        OkHttpUtils.objectMapper.writeValueAsString(betydninger),
                    )
                }
            }

            val kodeverk = parseTilKodeverk(respons)
            if (kodeverkNavn == "Arkivtemaer") {
                val betydninger = respons.betydninger["AVP"].orEmpty()
                LOG.info(
                    "AVP-diagnostikk kodeverk={} AVP-funnet={} antall-betydninger={} bokmålstermer={} AVP-verdi={}",
                    kodeverkNavn,
                    kodeverk.containsKey("AVP"),
                    betydninger.size,
                    betydninger.map { it.beskrivelser["nb"]?.term },
                    kodeverk["AVP"],
                )
            }
            return EnhetligKodeverk.Kodeverk(kodeverkNavn, kodeverk)
        }

        companion object {
            private val LOG = LoggerFactory.getLogger(Provider::class.java)
        }
    }

    internal fun parseTilKodeverk(respons: GetKodeverkKoderBetydningerResponseDTO): Map<String, String> =
        respons.betydninger.mapValues { entry ->
            entry.value
                .first()
                .beskrivelser["nb"]
                ?.term ?: entry.key
        }

    internal fun createKodeverkApi(
        machineToMachineTokenClient: MachineToMachineTokenClient,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
    ): KodeverkApi {
        val url = EnvironmentUtils.getRequiredProperty("FELLES_KODEVERK_URL")
        val downstreamApi = DownstreamApi.parse(EnvironmentUtils.getRequiredProperty("FELLES_KODEVERK_SCOPE"))

        val client =
            RestClient
                .baseClient()
                .newBuilder()
                .addInterceptor(XCorrelationIdInterceptor())
                .addInterceptor(
                    tjenestekallLoggingInterceptorFactory("Felleskodeverk") { request ->
                        requireNotNull(request.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    },
                ).addInterceptor(
                    AuthorizationInterceptor {
                        machineToMachineTokenClient.createMachineToMachineToken(downstreamApi)
                    },
                ).build()

        return KodeverkApi(url, client)
    }
}
