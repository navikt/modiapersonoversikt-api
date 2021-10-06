package no.nav.modiapersonoversikt.rest.journalforing

import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak
import no.nav.modiapersonoversikt.legacy.api.service.saker.SakerService
import no.nav.modiapersonoversikt.service.saker.EnhetIkkeSatt

interface JournalforingApi {
    fun hentSaker(fnr: String): SakerService.Resultat

    @Throws(EnhetIkkeSatt::class)
    fun knyttTilSak(fnr: String, traadId: String, sak: Sak, enhet: String)
}
