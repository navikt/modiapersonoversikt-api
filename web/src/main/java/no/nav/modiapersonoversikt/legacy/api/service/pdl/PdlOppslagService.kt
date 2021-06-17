package no.nav.modiapersonoversikt.legacy.api.service.pdl

import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentIdent
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentNavnBolk
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPerson
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.SokPersonUtenlandskID

interface PdlOppslagService {
    fun sokPersonUtenlandskID(utenlandskID: String): List<SokPersonUtenlandskID.PersonSearchHit>
    fun hentPerson(fnr: String): HentPerson.Person?
    fun hentIdent(fnr: String): HentIdent.Identliste?
    fun hentNavnBolk(fnrs: List<String>): Map<String, HentNavnBolk.Navn?>?
}
