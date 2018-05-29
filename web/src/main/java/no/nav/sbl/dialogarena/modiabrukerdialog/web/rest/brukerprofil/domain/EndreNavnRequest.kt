package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.brukerprofil.domain

data class EndreNavnRequest(
        var fødselsnummer: String = "",
        var fornavn: String = "",
        var mellomnavn: String = "",
        var etternavn: String = ""
)
