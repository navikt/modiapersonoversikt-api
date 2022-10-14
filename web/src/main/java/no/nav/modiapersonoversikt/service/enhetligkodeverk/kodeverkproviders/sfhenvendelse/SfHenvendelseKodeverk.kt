package no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.sfhenvendelse

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.apis.KodeverkApi
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.TemagruppeDTO
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseApiFactory
import no.nav.modiapersonoversikt.utils.createMachineToMachineToken

object SfHenvendelseKodeverk {
    class Provider(
        private val machineToMachineTokenClient: MachineToMachineTokenClient,
        private val sfHenvendelseKodeverk: KodeverkApi = createKodeverkApi(machineToMachineTokenClient)
    ) : EnhetligKodeverk.KodeverkProvider<String, String> {

        override fun hentKodeverk(kodeverkNavn: String): EnhetligKodeverk.Kodeverk<String, String> {
            val respons = sfHenvendelseKodeverk.henvendelseKodeverkTemagrupperGet(
                getCallId()
            )
            return EnhetligKodeverk.Kodeverk(kodeverkNavn, parseTilKodeverk(respons))
        }
    }

    internal fun parseTilKodeverk(respons: List<TemagruppeDTO>): Map<String, String> {
        return respons.associate { (navn, kode) ->
            kode to navn
        }
    }

    internal fun createKodeverkApi(machineToMachineTokenClient: MachineToMachineTokenClient): KodeverkApi {
        val downstreamApi = SfHenvendelseApiFactory.downstreamApi()
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
                    machineToMachineTokenClient.createMachineToMachineToken(downstreamApi)
                }
            )
            .build()

        return KodeverkApi(SfHenvendelseApiFactory.url(), client)
    }
}
