package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.PdlPersonResponse
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.PdlIdentResponse

interface PdlOppslagService {
    fun hentPerson(fnr: String): PdlPersonResponse?
    fun hentNavn(fnr: String): PdlPersonResponse?
    fun hentIdent(fnr: String): PdlIdentResponse?
}