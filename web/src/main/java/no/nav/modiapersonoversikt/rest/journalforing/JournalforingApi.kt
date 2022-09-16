package no.nav.modiapersonoversikt.rest.journalforing

import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import no.nav.modiapersonoversikt.service.journalforingsaker.SakerService

interface JournalforingApi {
    fun hentSaker(fnr: String): SakerService.Resultat

    @Throws(EnhetIkkeSatt::class)
    fun knyttTilSak(fnr: String, traadId: String, sak: JournalforingSak, enhet: String)

    class EnhetIkkeSatt(message: String) : IllegalArgumentException(message)
}
