package no.nav.kjerneinfo.consumer.fim.person.vergemal

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentPersonVergemaalEllerFullmakt
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import java.lang.RuntimeException
import kotlin.streams.toList

class PdlVergemalService(
    private val pdl: PdlOppslagService
    ) {

    fun hentVergemal(fodselsnummer: String): List<no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.PdlVerge> {
        val hentVergeResponse: List<HentPersonVergemaalEllerFullmakt.VergemaalEllerFremtidsfullmakt> = hentVergemalFraPdl(fodselsnummer)
        return hentVergeResponse.stream()
                .map { verge -> lagVergeDomeneObjekt(requireNotNull(verge)) }
                .toList()
    }

    fun hentVergemalFraPdl(fodselsnummer: String): List<HentPersonVergemaalEllerFullmakt.VergemaalEllerFremtidsfullmakt> {
        try {
            return pdl.hentPersonVergemaalEllerFullmakt(fodselsnummer)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun lagVergeDomeneObjekt(verge: HentPersonVergemaalEllerFullmakt.VergemaalEllerFremtidsfullmakt): no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.PdlVerge {
        val ident = verge.vergeEllerFullmektig.motpartsPersonident
        return no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.PdlVerge(
                ident = ident,
                personnavn = verge.vergeEllerFullmektig.navn,
                vergesakstype = verge.type,
                omfang = verge.vergeEllerFullmektig.omfang,
                embete = verge.embete,
                gyldighetstidspunkt = verge.folkeregistermetadata?.gyldighetstidspunkt,
                opphoerstidspunkt = verge.folkeregistermetadata?.opphoerstidspunkt
        )
    }
}