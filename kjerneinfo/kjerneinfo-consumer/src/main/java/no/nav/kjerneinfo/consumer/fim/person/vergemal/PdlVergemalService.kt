package no.nav.kjerneinfo.consumer.fim.person.vergemal

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentPersonVergemaalEllerFullmakt
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import java.lang.RuntimeException
import kotlin.streams.toList

class PdlVergemalService(
    private val pdl: PdlOppslagService
    ) {

    fun hentVergemal(fodselsnummer: String): List<no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.PdlVerge> {
        val hentVergeResponse: List<HentPersonVergemaalEllerFullmakt.VergemaalEllerFremtidsfullmakt?>? = hentVergemalFraPdl(fodselsnummer)
        requireNotNull(hentVergeResponse)
        return hentVergeResponse.stream()
                .map { verge -> lagVergeDomeneObjekt(verge) }
                .toList()
    }

    fun hentVergemalFraPdl(fodselsnummer: String): List<HentPersonVergemaalEllerFullmakt.VergemaalEllerFremtidsfullmakt?>? {
        try {
            return pdl.hentPersonVergemaalEllerFullmakt(fodselsnummer)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun lagVergeDomeneObjekt(verge: HentPersonVergemaalEllerFullmakt.VergemaalEllerFremtidsfullmakt?): no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.PdlVerge {
        requireNotNull(verge)
        val ident = verge.vergeEllerFullmektig.motpartsPersonident
        val omfang = verge.vergeEllerFullmektig.omfang
        val gyldighetstidspunkt = verge.folkeregistermetadata?.gyldighetstidspunkt
        val opphoerstidspunkt = verge.folkeregistermetadata?.opphoerstidspunkt
        return no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.PdlVerge()
                .withIdent(ident)
                .withPersonnavn(verge.vergeEllerFullmektig)
                .withVergesakstype(verge.type)
                .withOmfang(omfang)
                .withEmbete(verge.embete)
                .withGyldighetstidspunkt(gyldighetstidspunkt)
                .withOpphoerstidspunkt(opphoerstidspunkt)
    }
}