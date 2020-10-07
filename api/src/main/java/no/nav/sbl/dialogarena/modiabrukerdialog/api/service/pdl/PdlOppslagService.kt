package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentIdent
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentNavnBolk
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentPerson
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.SokPerson

interface PdlOppslagService {
    fun sokPerson(utenlandskId: String): SokPerson.SearchResult.Person.utenlandskidentifikasjonsnummer.identifikasjonsnummer?
    fun hentPerson(fnr: String): HentPerson.Person?
    fun hentIdent(fnr: String): HentIdent.Identliste?
    fun hentNavnBolk(fnrs: List<String>): Map<String, HentNavnBolk.Navn?>?
}
