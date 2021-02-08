package no.nav.sbl.dialogarena.modiabrukerdialog.api.service

interface FodselnummerAktorService {
    fun hentAktorIdForFnr(fnr: String): String
    fun hentFnrForAktorId(aktorId: String): String
}
