package no.nav.modiapersonoversikt.service.saker.kilder

import no.nav.modiapersonoversikt.legacy.api.domain.bidragsak.generated.apis.BidragSakControllerApi
import no.nav.modiapersonoversikt.legacy.api.domain.bidragsak.generated.models.BidragSakDto
import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak
import no.nav.modiapersonoversikt.service.saker.SakerKilde
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService

internal class BidragSaker(
    private val client: BidragSakControllerApi,
    private val unleashService: UnleashService
) : SakerKilde {
    override val kildeNavn: String = "BIDRAG"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        val hentDataFraBisys = unleashService.isEnabled(Feature.HENT_BISYS_SAKER)
        val sakerFra = if (hentDataFraBisys) client.find(fnr) else emptyList()

        /**
         * Må fjerne tidligere generelle saker lagt til for BID fra GenerelleSaker.
         * Dette må gjøres fordi disse sakene ikke nødvendigvis blir lagt til med riktige markør-felt.
         */
        saker.removeIf { it.temaKode == "BID" }

        saker.addAll(sakerFra.map(::tilSak))
        saker.add(generellBidragsSak(hentDataFraBisys))
    }

    companion object {
        private fun generellBidragsSak(hentDataFraBisys: Boolean = true): Sak {
            return Sak().apply {
                saksId = "-"
                fagsystemSaksId = "-"
                temaKode = "BID"
                temaNavn = "Bidrag"
                fagsystemKode = Sak.FAGSYSTEMKODE_BIDRAG
                fagsystemNavn = "Kopiert inn i Bisys"
                sakstype = if (hentDataFraBisys) Sak.SAKSTYPE_GENERELL else Sak.SAKSTYPE_MED_FAGSAK
                opprettetDato = null
                finnesIGsak = false
                finnesIPsak = false
            }
        }

        private fun tilSak(bidragSakDto: BidragSakDto) = generellBidragsSak().apply {
            saksId = bidragSakDto.saksnummer
            sakstype = Sak.SAKSTYPE_MED_FAGSAK
            fagsystemKode = Sak.FAGSYSTEMKODE_BIDRAG
            temaKode = Sak.FAGSYSTEMKODE_BIDRAG
        }
    }
}
