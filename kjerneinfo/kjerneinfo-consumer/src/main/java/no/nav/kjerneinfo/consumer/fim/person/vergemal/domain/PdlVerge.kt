package no.nav.kjerneinfo.consumer.fim.person.vergemal.domain

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentPersonVergemaalEllerFullmakt

class PdlVerge(

) {
    private var identVerge: String? = null
    private var personnavnVerge: HentPersonVergemaalEllerFullmakt.Personnavn2? = null
    private var vergesakstype: String? = null
    private var vergeEmbete: String? = null
    private var vergeOmfang: String? = null
    private var gyldighetstidspunkt: HentPersonVergemaalEllerFullmakt.DateTime? = null
    private var opphoerstidspunkt: HentPersonVergemaalEllerFullmakt.DateTime? = null

    fun withIdent(ident: String?): PdlVerge {
        identVerge = ident
        return this
    }

    fun withPersonnavn(verge: HentPersonVergemaalEllerFullmakt.VergeEllerFullmektig): PdlVerge {
        personnavnVerge = verge.navn
        return this
    }

    fun withVergesakstype(type: String?): PdlVerge {
        vergesakstype = type
        return this
    }

    fun withOmfang(omfang: String?): PdlVerge {
        vergeOmfang = omfang
        return this
    }

    fun withGyldighetstidspunkt(gyldigTidspunkt: HentPersonVergemaalEllerFullmakt.DateTime?): PdlVerge {
        gyldighetstidspunkt = gyldigTidspunkt
        return this
    }

    fun withOpphoerstidspunkt(opphoer: HentPersonVergemaalEllerFullmakt.DateTime?): PdlVerge {
        opphoerstidspunkt = opphoer
        return this
    }

    fun withEmbete(embete: String?): PdlVerge {
        vergeEmbete = embete
        return this
    }

    fun getIdent(): String? {
        return identVerge
    }

    fun getPersonnavn(): HentPersonVergemaalEllerFullmakt.Personnavn2? {
        return personnavnVerge
    }

    fun getVergesakstype(): String? {
        return vergesakstype
    }
    
    fun getEmbete(): String? {
        return vergeEmbete
    }

    fun getOmfang(): String? {
        return vergeOmfang
    }

    fun getGyldighetstidspunkt(): HentPersonVergemaalEllerFullmakt.DateTime? {
        return gyldighetstidspunkt
    }

    fun getOpphoerstidspunkt(): HentPersonVergemaalEllerFullmakt.DateTime? {
        return opphoerstidspunkt
    }
}