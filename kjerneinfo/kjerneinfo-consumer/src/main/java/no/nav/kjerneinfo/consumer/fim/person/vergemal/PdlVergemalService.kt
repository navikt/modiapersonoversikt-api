package no.nav.kjerneinfo.consumer.fim.person.vergemal

import no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.PdlVerge
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentPersonVergemaalEllerFullmakt
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import java.lang.RuntimeException

class PdlVergemalService(
    private val pdl: PdlOppslagService
    ) {

    fun hentVergemal(fodselsnummer: String): List<PdlVerge> {
        val hentVergeResponse: List<HentPersonVergemaalEllerFullmakt.VergemaalEllerFremtidsfullmakt> = hentVergemalFraPdl(fodselsnummer)
        return hentVergeResponse
                .map { verge -> lagVergeDomeneObjekt(verge) }
    }

    fun hentVergemalFraPdl(fodselsnummer: String): List<HentPersonVergemaalEllerFullmakt.VergemaalEllerFremtidsfullmakt> {
        try {
            return pdl.hentPersonVergemaalEllerFullmakt(fodselsnummer)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun lagVergeDomeneObjekt(verge: HentPersonVergemaalEllerFullmakt.VergemaalEllerFremtidsfullmakt): PdlVerge {
        val ident = getIdentFromVerge(verge.vergeEllerFullmektig)
        val navn = if (ident != null) verge.vergeEllerFullmektig?.navn else null
        return PdlVerge(
                ident = ident,
                personnavn = navn,
                vergesakstype = verge.type,
                omfang = verge.vergeEllerFullmektig.omfang,
                embete = verge.embete,
                gyldighetstidspunkt = verge.folkeregistermetadata?.gyldighetstidspunkt,
                opphoerstidspunkt = verge.folkeregistermetadata?.opphoerstidspunkt
        )
    }

    private fun getIdentFromVerge(verge: HentPersonVergemaalEllerFullmakt.VergeEllerFullmektig?): String? {
        val vergeAktoer = verge
        return if (vergeAktoer is HentPersonVergemaalEllerFullmakt.VergeEllerFullmektig) {
            return vergeAktoer.motpartsPersonident
        } else {
            throw RuntimeException("Ident for vegemal er av ukjent type")
        }
    }
}