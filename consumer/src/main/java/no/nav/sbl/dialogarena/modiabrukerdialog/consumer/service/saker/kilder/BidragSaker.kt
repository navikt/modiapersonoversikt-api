package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerKilde

internal class BidragSaker : SakerKilde {
    override val kildeNavn: String
        get() = "BIDRAG"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        val bidragSak = Sak()
        bidragSak.saksId = "-"
        bidragSak.fagsystemSaksId = "-"
        bidragSak.temaKode = Sak.BIDRAG_MARKOR
        bidragSak.temaNavn = "Bidrag"
        bidragSak.fagsystemKode = Sak.BIDRAG_MARKOR
        bidragSak.fagsystemNavn = "Kopiert inn i Bisys"
        bidragSak.sakstype = Sak.SAKSTYPE_MED_FAGSAK
        bidragSak.opprettetDato = null
        bidragSak.finnesIGsak = false
        bidragSak.finnesIPsak = false
        bidragSak.syntetisk = true
        saker.add(bidragSak)
    }
}
