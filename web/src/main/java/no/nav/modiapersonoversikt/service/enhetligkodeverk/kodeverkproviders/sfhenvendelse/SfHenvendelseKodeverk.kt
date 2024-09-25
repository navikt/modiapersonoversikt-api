package no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.sfhenvendelse

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.apis.KodeverkApi
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.TemagruppeDTO
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseApiFactory
import no.nav.modiapersonoversikt.utils.createMachineToMachineToken
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

object SfHenvendelseKodeverk {
    class Provider(
        private val machineToMachineTokenClient: MachineToMachineTokenClient,
        private val tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
        private val sfHenvendelseKodeverk: KodeverkApi =
            createKodeverkApi(
                machineToMachineTokenClient,
                tjenestekallLoggingInterceptorFactory,
            ),
    ) : EnhetligKodeverk.KodeverkProvider<String, String> {
        override fun hentKodeverk(kodeverkNavn: String): EnhetligKodeverk.Kodeverk<String, String> {
            val respons =
                sfHenvendelseKodeverk.henvendelseKodeverkTemagrupperGet(
                    getCallId(),
                ) ?: throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Feil ved henting av kodverk.",
                )

            return EnhetligKodeverk.Kodeverk(kodeverkNavn, parseTilKodeverk(respons))
        }
    }

    internal fun parseTilKodeverk(respons: List<TemagruppeDTO>): Map<String, String> =
        respons.associate { (navn, kode) ->
            kode to navn
        }

    internal fun createKodeverkApi(
        machineToMachineTokenClient: MachineToMachineTokenClient,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
    ): KodeverkApi {
        val downstreamApi = SfHenvendelseApiFactory.downstreamApi()
        val client =
            RestClient
                .baseClient()
                .newBuilder()
                .addInterceptor(
                    tjenestekallLoggingInterceptorFactory("SF-Henvendelse-Kodeverk") { request ->
                        requireNotNull(request.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    },
                ).addInterceptor(
                    AuthorizationInterceptor {
                        machineToMachineTokenClient.createMachineToMachineToken(downstreamApi)
                    },
                ).build()

        return KodeverkApi(SfHenvendelseApiFactory.url(), client)
    }
}
