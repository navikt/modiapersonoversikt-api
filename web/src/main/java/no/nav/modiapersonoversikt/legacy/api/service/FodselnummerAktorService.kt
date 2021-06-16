package no.nav.modiapersonoversikt.legacy.api.service

interface FodselnummerAktorService {
    fun hentAktorIdForFnr(fnr: String): String?
    fun hentFnrForAktorId(aktorId: String): String?
}
