package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentIdent
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentNavnBolk
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentPerson

interface PdlOppslagService {
    fun hentPerson(fnr: String): HentPerson.Person?
    fun hentPersonBasertPaUtenlandskId(utenlandskId: String): HentPersonBasertPaUtenlandskId.Person?
    fun hentIdent(fnr: String): HentIdent.Identliste?
    fun hentNavnBolk(fnrs: List<String>): Map<String, HentNavnBolk.Navn?>?
}
