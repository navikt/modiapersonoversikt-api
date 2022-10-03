package no.nav.modiapersonoversikt.service.journalforingsaker.kilder

import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import no.nav.modiapersonoversikt.service.journalforingsaker.SakerKilde
import org.joda.time.DateTime

internal class GenerelleSaker : SakerKilde {
    override val kildeNavn: String
        get() = "GENERELLE"

    override fun leggTilSaker(fnr: String, saker: MutableList<JournalforingSak>) {
        val generelleSaker = saker
            .filter { obj: JournalforingSak -> obj.isSakstypeForVisningGenerell }

        val manglendeGenerelleSaker = JournalforingSak.GODKJENTE_TEMA_FOR_GENERELL_SAK
            .filter { temakode: String -> harIngenSakerMedTemakode(temakode, generelleSaker) && JournalforingSak.TEMAKODE_OPPFOLGING != temakode }
            .map { temakode: String -> lagGenerellSakMedTema(temakode) }

        saker.addAll(manglendeGenerelleSaker)
    }

    companion object {
        private fun harIngenSakerMedTemakode(temakode: String, generelleSaker: List<JournalforingSak>): Boolean {
            return generelleSaker.none { it.temaKode == temakode }
        }

        private fun lagGenerellSakMedTema(temakode: String): JournalforingSak {
            return JournalforingSak().apply {
                temaKode = temakode
                finnesIGsak = false
                fagsystemKode = JournalforingSak.FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK
                sakstype = JournalforingSak.SAKSTYPE_GENERELL
                opprettetDato = DateTime.now()
            }
        }
    }
}
