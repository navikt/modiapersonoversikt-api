package no.nav.modiapersonoversikt.rest.journalforing

import no.nav.modiapersonoversikt.service.saker.EnhetIkkeSatt
import no.nav.modiapersonoversikt.service.saker.Sak
import no.nav.modiapersonoversikt.service.saker.SakerService

interface JournalforingApi {
    fun hentSaker(fnr: String): SakerService.Resultat

    @Throws(EnhetIkkeSatt::class)
    fun knyttTilSak(fnr: String, traadId: String, sak: Sak, enhet: String)
}
