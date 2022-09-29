package no.nav.modiapersonoversikt.service.journalforingsaker.kilder

import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import no.nav.modiapersonoversikt.service.journalforingsaker.SakerKilde

internal class BidragSaker : SakerKilde {
    override val kildeNavn: String = "BIDRAG"

    override fun leggTilSaker(fnr: String, saker: MutableList<JournalforingSak>) {
        val bidragSak = JournalforingSak().apply {
            saksId = "-"
            fagsystemSaksId = "-"
            temaKode = "BID"
            temaNavn = "Bidrag"
            fagsystemKode = JournalforingSak.FAGSYSTEMKODE_BIDRAG
            fagsystemNavn = "Kopiert inn i Bisys"
            sakstype = JournalforingSak.SAKSTYPE_MED_FAGSAK
            opprettetDato = null
            finnesIGsak = false
            finnesIPsak = false
        }

        saker.add(bidragSak)
    }
}
