package no.nav.modiapersonoversikt.service.saker.kilder

import no.nav.modiapersonoversikt.legacy.api.domain.bidragsak.generated.apis.BidragSakControllerApi
import no.nav.modiapersonoversikt.legacy.api.domain.bidragsak.generated.models.BidragSakDto
import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak
import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak.BIDRAG_MARKOR
import no.nav.modiapersonoversikt.service.saker.SakerKilde

internal class BidragSaker(private val client: BidragSakControllerApi) : SakerKilde {
    override val kildeNavn: String = "BIDRAG"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        val sakerFra = client.find(fnr)

        val tilSaker = sakerFra.map { BIDRAGSAK_TIL_SAK.invoke(it) }
        saker.addAll(tilSaker)
        saker.add(generellBidragsSak())
    }

    companion object {
        fun generellBidragsSak(): Sak {
            return Sak().apply {
                saksId = "-"
                fagsystemSaksId = "-"
                temaKode = BIDRAG_MARKOR
                temaNavn = "Bidrag"
                fagsystemKode = ""
                fagsystemNavn = "Kopiert inn i Bisys"
                sakstype = Sak.SAKSTYPE_GENERELL
                opprettetDato = null
                finnesIGsak = false
                finnesIPsak = false
            }
        }

        val BIDRAGSAK_TIL_SAK = { bidragSakDto: BidragSakDto ->
            generellBidragsSak().apply {
                saksId = bidragSakDto.saksnummer
                sakstype = Sak.SAKSTYPE_MED_FAGSAK
                fagsystemKode = Sak.FAGSYSTEMKODE_BIDRAG
                temaKode = Sak.FAGSYSTEMKODE_BIDRAG
            }
        }
    }
}
