package no.nav.modiapersonoversikt.service.saker.kilder

import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.SubjectHandler
import no.nav.modiapersonoversikt.legacy.api.domain.bidragsak.generated.models.BidragSakDto
import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak
import no.nav.modiapersonoversikt.service.saker.SakerKilde
import no.nav.modiapersonoversikt.service.saker.mediation.BidragApiFactory

internal class BidragSaker : SakerKilde {
    override val kildeNavn: String
        get() = "BIDRAG"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {

        val bidragSakControllerApi = BidragApiFactory.createClient {
            SubjectHandler.getSsoToken(SsoToken.Type.OIDC)
                .orElseThrow { IllegalStateException("Fant ikke OIDC-token") }
        }

        val sakerFra = bidragSakControllerApi.find(fnr)

        val tilSaker = sakerFra.map { BIDRAGSAK_TIL_SAK }.ifEmpty {
            sakInstant()
        }
        saker.addAll(listOf(tilSaker as Sak))
    }

    companion object {
        fun sakInstant(): Sak {
            return Sak().apply {
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
        }

        val BIDRAGSAK_TIL_SAK = { bidragSakDto: BidragSakDto ->
            sakInstant().apply {
                saksId = bidragSakDto.saksnummer
            }
        }
    }
}
