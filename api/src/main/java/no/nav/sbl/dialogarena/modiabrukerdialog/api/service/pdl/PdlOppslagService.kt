package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.PdlPersonResponse

interface PdlOppslagService {
    fun hentPerson(fnr: String): PdlPersonResponse?
    fun hentNavn(fnr: String): PdlPersonResponse?
}