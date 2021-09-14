package no.nav.modiapersonoversikt.service.saker.kilder

import no.nav.modiapersonoversikt.legacy.api.domain.bidragsak.generated.apis.BidragSakControllerApi
import no.nav.modiapersonoversikt.legacy.api.domain.bidragsak.generated.models.BidragSakDto
import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak
import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak.BIDRAG_MARKOR
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

        saker.addAll(sakerFra.map(::tilSak))
        saker.add(generellBidragsSak(hentDataFraBisys))
    }

    companion object {
        private fun generellBidragsSak(hentDataFraBisys: Boolean = true): Sak {
            return Sak().apply {
                saksId = "-"
                fagsystemSaksId = "-"
                temaKode = BIDRAG_MARKOR
                temaNavn = "Bidrag"
                fagsystemKode = ""
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
