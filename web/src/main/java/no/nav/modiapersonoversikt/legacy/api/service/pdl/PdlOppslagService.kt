package no.nav.modiapersonoversikt.legacy.api.service.pdl

import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentIdenter
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentNavnBolk
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPerson
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.SokPersonUtenlandskID

interface PdlOppslagService {
    fun sokPersonUtenlandskID(utenlandskID: String): List<SokPersonUtenlandskID.PersonSearchHit>
    fun hentPerson(fnr: String): HentPerson.Person?
    fun hentIdenter(fnr: String): HentIdenter.Identliste?
    fun hentAktorId(fnr: String): String?
    fun hentNavnBolk(fnrs: List<String>): Map<String, HentNavnBolk.Navn?>?
}
