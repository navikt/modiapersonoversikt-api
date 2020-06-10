package no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl

data class PdlRequest(val query: String, val variables: Variables)

data class PdlIdentRequest(val query: String, val variables: IdentVariables)

data class Variables(val ident: String, val navnHistorikk: Boolean = false)

data class IdentVariables(val ident: String, val gruppe: String, val identhistorikk: Boolean = false)