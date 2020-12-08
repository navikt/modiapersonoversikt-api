package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentIdent
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentNavnBolk
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentPerson
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.SokPersonUtenlandskID

interface PdlOppslagService {
    fun sokPersonUtenlandskID(utenlandskID: String): List<SokPersonUtenlandskID.SearchHit>
    fun hentPerson(ident: String): HentPerson.Person?
    fun hentIdent(ident: String): HentIdent.Identliste?
    fun hentNavnBolk(identer: List<String>): Map<String, HentNavnBolk.Navn?>?
}
