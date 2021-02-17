package no.nav.kjerneinfo.consumer.fim.person.vergemal

import no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.PdlVerge
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentNavnBolk
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentPersonVergemaalEllerFullmakt
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import java.lang.RuntimeException
import java.util.stream.Collectors
import kotlin.streams.toList

class PdlVergemalService(
    private val pdl: PdlOppslagService
    ) {

    val PDL_VERGES_FNR_MANGLENDE_DATA = "00000000000"

    fun hentVergemal(fodselsnummer: String): List<PdlVerge> {
        val hentVergeResponse: List<HentPersonVergemaalEllerFullmakt.VergemaalEllerFremtidsfullmakt> = hentVergemalFraPdl(fodselsnummer)
        val vergeIdenter: List<String> = pdl.hentPersonVergemaalEllerFullmakt(fodselsnummer)
                .stream()
                .map { verge: HentPersonVergemaalEllerFullmakt.VergemaalEllerFremtidsfullmakt? -> getIdentFromVerge(verge?.vergeEllerFullmektig) }
                .collect(Collectors.toList<String>())
        val vergeNavn = pdl.hentNavnBolk(vergeIdenter)
        return hentVergeResponse.stream()
                .map { verge -> lagVergeDomeneObjekt(verge, vergeNavn) }
                .toList()
    }

    fun hentVergemalFraPdl(fodselsnummer: String): List<HentPersonVergemaalEllerFullmakt.VergemaalEllerFremtidsfullmakt> {
        try {
            return pdl.hentPersonVergemaalEllerFullmakt(fodselsnummer)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun lagVergeDomeneObjekt(verge: HentPersonVergemaalEllerFullmakt.VergemaalEllerFremtidsfullmakt, vergeNavn: Map<String, HentNavnBolk.Navn?>?): PdlVerge {
        val ident = getIdentFromVerge(verge.vergeEllerFullmektig)
        val navn: HentNavnBolk.Navn? = vergeNavn?.get(getIdentFromVerge(verge.vergeEllerFullmektig))
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
            val ident = vergeAktoer.motpartsPersonident
            if (ident == PDL_VERGES_FNR_MANGLENDE_DATA) null else ident
        } else {
            throw RuntimeException("Ident for vegemal er av ukjent type")
        }
    }
}