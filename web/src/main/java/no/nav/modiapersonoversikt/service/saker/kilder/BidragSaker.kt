package no.nav.modiapersonoversikt.service.saker.kilder

import no.nav.modiapersonoversikt.service.saker.Sak
import no.nav.modiapersonoversikt.service.saker.SakerKilde

internal class BidragSaker : SakerKilde {
    override val kildeNavn: String = "BIDRAG"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        val bidragSak = Sak().apply {
            saksId = "-"
            fagsystemSaksId = "-"
            temaKode = "BID"
            temaNavn = "Bidrag"
            fagsystemKode = Sak.FAGSYSTEMKODE_BIDRAG
            fagsystemNavn = "Kopiert inn i Bisys"
            sakstype = Sak.SAKSTYPE_MED_FAGSAK
            opprettetDato = null
            finnesIGsak = false
            finnesIPsak = false
        }

        saker.add(bidragSak)
    }
}
