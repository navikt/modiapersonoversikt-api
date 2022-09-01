package no.nav.modiapersonoversikt.service.saker.kilder

import no.nav.modiapersonoversikt.consumer.saf.generated.HentBrukersSaker
import no.nav.modiapersonoversikt.legacy.sak.service.saf.SafService
import no.nav.modiapersonoversikt.service.saker.Sak
import no.nav.modiapersonoversikt.service.saker.SakerKilde
import org.joda.time.DateTime
import java.time.LocalDateTime
import java.time.ZoneId

class SafSaker(private val service: SafService) : SakerKilde {
    override val kildeNavn: String = "SAF"
    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        val resultat = service.hentSaker(fnr)
            .data
            ?.saker
            ?.filterNotNull()
            ?.map {
                Sak().apply {
                    opprettetDato = it.datoOpprettet?.value?.let { convertJavaDateTimeToJoda(it) }
                    saksId = it.arkivsaksnummer
                    fagsystemSaksId = it.fagsakId
                    temaKode = it.tema?.name ?: ""
                    fagsystemKode = it.fagsaksystem ?: ""
                    sakstype = when (it.sakstype) {
                        HentBrukersSaker.Sakstype.FAGSAK -> "MFS"
                        HentBrukersSaker.Sakstype.GENERELL_SAK -> "GEN"
                        else -> throw IllegalStateException("Ukjent sakstype: ${it.sakstype}")
                    }
                }
            }
            ?: emptyList()

        saker.addAll(resultat)
    }

    companion object {
        private fun convertJavaDateTimeToJoda(dateTime: LocalDateTime): DateTime {
            val epochMillies = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            return DateTime(epochMillies).withTimeAtStartOfDay()
        }
    }
}
