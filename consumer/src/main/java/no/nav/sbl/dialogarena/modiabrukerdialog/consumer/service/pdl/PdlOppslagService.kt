package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl.generated.HentIdent
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl.generated.HentNavn
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl.generated.HentPerson

interface PdlOppslagService {
    fun hentPerson(fnr: String): HentPerson.Person?
    fun hentNavn(fnr: String): HentNavn.Person?
    fun hentIdent(fnr: String): HentIdent.Identliste?
}
