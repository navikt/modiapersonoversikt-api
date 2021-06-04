package no.nav.modiapersonoversikt.api.service

interface FodselnummerAktorService {
    fun hentAktorIdForFnr(fnr: String): String?
    fun hentFnrForAktorId(aktorId: String): String?
}
