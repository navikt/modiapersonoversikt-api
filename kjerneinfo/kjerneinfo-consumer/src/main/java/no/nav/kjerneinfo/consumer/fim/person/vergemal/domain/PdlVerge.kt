package no.nav.kjerneinfo.consumer.fim.person.vergemal.domain

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentPersonVergemaalEllerFullmakt

data class PdlVerge(
        val ident: String?,
        val personnavn: HentPersonVergemaalEllerFullmakt.Personnavn?,
        val vergesakstype: String?,
        val embete: String?,
        val omfang: String?,
        val gyldighetstidspunkt: HentPersonVergemaalEllerFullmakt.DateTime?,
        val opphoerstidspunkt: HentPersonVergemaalEllerFullmakt.DateTime?
)