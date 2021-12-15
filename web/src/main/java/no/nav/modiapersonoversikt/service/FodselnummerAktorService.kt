package no.nav.modiapersonoversikt.service

import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService
import org.springframework.beans.factory.annotation.Autowired

interface FodselnummerAktorService {
    fun hentAktorIdForFnr(fnr: String): String?
    fun hentFnrForAktorId(aktorId: String): String?
}

open class FodselnummerAktorServiceImpl @Autowired constructor(
    val pdlOppslagService: PdlOppslagService
) : FodselnummerAktorService {
    override fun hentAktorIdForFnr(fnr: String): String {
        return requireNotNull(pdlOppslagService.hentAktorId(fnr)) {
            "Feil ved henting av aktorId for $fnr"
        }
    }

    override fun hentFnrForAktorId(aktorId: String): String {
        return requireNotNull(pdlOppslagService.hentFnr(aktorId)) {
            "Feil ved henting av fnr for $aktorId"
        }
    }
}
