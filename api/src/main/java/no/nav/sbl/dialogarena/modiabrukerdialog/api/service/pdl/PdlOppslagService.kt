package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.*

interface PdlOppslagService {
    fun sokPersonUtenlandskID(utenlandskID: String): List<SokPersonUtenlandskID.SearchHit>
    fun hentPerson(ident: String): HentPerson.Person?
    fun hentIdent(ident: String): HentIdent.Identliste?
    fun hentNavnBolk(identer: List<String>): Map<String, HentNavnBolk.Navn?>?
    fun hentPersonVergemaalEllerFullmakt(ident: String): List<HentPersonVergemaalEllerFullmakt.VergemaalEllerFremtidsfullmakt?>
}
