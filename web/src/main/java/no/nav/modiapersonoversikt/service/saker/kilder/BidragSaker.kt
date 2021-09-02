package no.nav.modiapersonoversikt.service.saker.kilder

import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.SubjectHandler
import no.nav.modiapersonoversikt.legacy.api.domain.bidragsak.generated.models.BidragSakDto
import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak
import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak.BIDRAG_MARKOR
import no.nav.modiapersonoversikt.service.saker.SakerKilde
import no.nav.modiapersonoversikt.service.saker.mediation.BidragApiClient

internal class BidragSaker(private val bidragApiClient: BidragApiClient) : SakerKilde {
    override val kildeNavn: String
        get() = "BIDRAG"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        val bidragSakControllerApi = bidragApiClient.createClient {
            SubjectHandler.getSsoToken(SsoToken.Type.OIDC)
                .orElseThrow { IllegalStateException("Fant ikke OIDC-token") }
        }

        val sakerFra = bidragSakControllerApi.find(fnr)

        val tilSaker = sakerFra.map { BIDRAGSAK_TIL_SAK.invoke(it) }
        saker.addAll(tilSaker)
        saker.add(sakInstant())
    }

    companion object {
        fun sakInstant(): Sak {
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
            sakInstant().apply {
                saksId = bidragSakDto.saksnummer
                sakstype = Sak.SAKSTYPE_MED_FAGSAK
                fagsystemKode = Sak.FAGSYSTEMKODE_BIDRAG
                temaKode = Sak.FAGSYSTEMKODE_BIDRAG
            }
        }
    }
}
