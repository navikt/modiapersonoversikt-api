package no.nav.modiapersonoversikt.service.journalforingsaker.kilder

import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import no.nav.modiapersonoversikt.service.journalforingsaker.SakerKilde
import org.joda.time.DateTime

internal class GenerelleSaker : SakerKilde {
    override val kildeNavn: String
        get() = "GENERELLE"

    override fun leggTilSaker(
        fnr: String,
        saker: MutableList<JournalforingSak>,
    ) {
        val generelleSaker =
            JournalforingSak.GODKJENTE_TEMA_FOR_GENERELL_SAK.map { temakode: String -> lagGenerellSakMedTema(temakode) }
        saker.addAll(generelleSaker)
    }

    companion object {
        private fun lagGenerellSakMedTema(temakode: String): JournalforingSak =
            JournalforingSak().apply {
                temaKode = temakode
                finnesIGsak = false
                fagsystemKode = JournalforingSak.FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK
                sakstype = JournalforingSak.SAKSTYPE_GENERELL
                opprettetDato = DateTime.now()
            }
    }
}
