package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerKilde

internal class BidragSaker : SakerKilde {
    override val kildeNavn: String
        get() = "BIDRAG"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        val bidragSak = Sak().apply {
            saksId = "-"
            fagsystemSaksId = "-"
            temaKode = Sak.BIDRAG_MARKOR
            temaNavn = "Bidrag"
            fagsystemKode = Sak.BIDRAG_MARKOR
            fagsystemNavn = "Kopiert inn i Bisys"
            sakstype = Sak.SAKSTYPE_MED_FAGSAK
            opprettetDato = null
            finnesIGsak = false
            finnesIPsak = false
            syntetisk = true
        }
        saker.add(bidragSak)
    }
}
