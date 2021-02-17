package no.nav.kjerneinfo.consumer.fim.person.vergemal.domain

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentPersonVergemaalEllerFullmakt

open class PdlVerge(
        private val ident: String?,
        private val personnavn: HentPersonVergemaalEllerFullmakt.Personnavn?,
        private val vergesakstype: String?,
        private val embete: String?,
        private val omfang: String?,
        private val gyldighetstidspunkt: HentPersonVergemaalEllerFullmakt.DateTime?,
        private val opphoerstidspunkt: HentPersonVergemaalEllerFullmakt.DateTime?
) {

    fun getIdent(): String? {
        return ident
    }

    fun getPersonnavn(): HentPersonVergemaalEllerFullmakt.Personnavn? {
        return personnavn
    }

    fun getVergesakstype(): String? {
        return vergesakstype
    }
    
    fun getEmbete(): String? {
        return embete
    }

    fun getOmfang(): String? {
        return omfang
    }

    fun getGyldighetstidspunkt(): HentPersonVergemaalEllerFullmakt.DateTime? {
        return gyldighetstidspunkt
    }

    fun getOpphoerstidspunkt(): HentPersonVergemaalEllerFullmakt.DateTime? {
        return opphoerstidspunkt
    }
}