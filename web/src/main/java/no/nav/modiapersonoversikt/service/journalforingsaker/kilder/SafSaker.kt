package no.nav.modiapersonoversikt.service.journalforingsaker.kilder

import no.nav.modiapersonoversikt.consumer.saf.generated.enums.Sakstype
import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import no.nav.modiapersonoversikt.service.journalforingsaker.SakerKilde
import no.nav.modiapersonoversikt.service.saf.SafService
import org.joda.time.DateTime
import java.time.LocalDateTime
import java.time.ZoneId

class SafSaker(private val service: SafService) : SakerKilde {
    override val kildeNavn: String = "SAF"

    override fun leggTilSaker(
        fnr: String,
        saker: MutableList<JournalforingSak>,
    ) {
        val resultat =
            service.hentSaker(fnr)
                .data
                ?.saker
                ?.filterNotNull()
                ?.map {
                    JournalforingSak().apply {
                        opprettetDato = it.datoOpprettet?.let { convertJavaDateTimeToJoda(it) }
                        saksId = it.arkivsaksnummer
                        fagsystemSaksId = it.fagsakId
                        temaKode = it.tema?.name ?: ""
                        fagsystemKode = it.fagsaksystem ?: ""
                        sakstype =
                            when (it.sakstype) {
                                Sakstype.FAGSAK -> "MFS"
                                Sakstype.GENERELL_SAK -> "GEN"
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
