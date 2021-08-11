package no.nav.modiapersonoversikt.legacy.api.service.pdl

import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.*

interface PdlOppslagService {
    fun sokPersonUtenlandskID(utenlandskID: String): List<SokPersonUtenlandskID.PersonSearchHit>
    fun hentPerson(fnr: String): HentPerson.Person?
    fun hentIdent(fnr: String): HentIdent.Identliste?
    fun hentNavnBolk(fnrs: List<String>): Map<String, HentNavnBolk.Navn>
    fun hentGeografiskTilknyttning(fnr: String): HentGt.GeografiskTilknytning?
}
