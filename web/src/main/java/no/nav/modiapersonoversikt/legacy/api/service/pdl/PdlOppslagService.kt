package no.nav.modiapersonoversikt.legacy.api.service.pdl

import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.*

interface PdlOppslagService {
    fun sokPersonUtenlandskID(utenlandskID: String): List<SokPersonUtenlandskID.PersonSearchHit>
    fun hentPerson(fnr: String): HentPerson.Person?
    fun hentPersondata(fnr: String): HentPersondata.Person?
    fun hentPersondataLite(fnr: List<String>): List<HentPersondataLite.HentPersonBolkResult>
    fun hentGeografiskTilknyttning(fnr: String): String?
    fun hentIdenter(fnr: String): HentIdenter.Identliste?
    fun hentAktorId(fnr: String): String?
    fun hentNavnBolk(fnrs: List<String>): Map<String, HentNavnBolk.Navn?>?
}
