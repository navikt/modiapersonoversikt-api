package no.nav.modiapersonoversikt.service.enhetligkodeverk

import no.nav.common.log.MDCConstants
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
        return hentKodeverk(respons)
    }

    fun fraSfHenvendelseKodeverk(): EnhetligKodeverk.Kodeverk {
        val respons = sfHenvendelseKodeverk.henvendelseKodeverkTemagrupperGet(
            MDC.get(MDCConstants.MDC_CALL_ID) ?: UUID.randomUUID().toString()
        )

        return hentKodeverk(respons)
    }

    private fun hentKodeverk(respons: List<TemagruppeDTO>): EnhetligKodeverk.Kodeverk {
        val kodeverk = respons.map { (navn, kode) ->
            kode to navn
        }.toMap()

        return EnhetligKodeverk.Kodeverk(kodeverk)
    }

    private fun hentKodeverk(respons: GetKodeverkKoderBetydningerResponseDTO): EnhetligKodeverk.Kodeverk {
        val kodeverk = respons.betydninger.mapValues { entry ->
            entry.value.first().beskrivelser["nb"]?.term ?: entry.key
        }
        return EnhetligKodeverk.Kodeverk(kodeverk)
    }
}
