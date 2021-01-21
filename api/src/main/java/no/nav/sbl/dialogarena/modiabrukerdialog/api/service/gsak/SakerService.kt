package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet


interface SakerService {
    class Resultat @JvmOverloads constructor(var saker: List<Sak> = ArrayList(), var feiledeSystemer: List<String?> = ArrayList())

    fun hentSaker(fnr: String): Resultat

    @Deprecated("")
    fun hentPensjonSaker(fnr: String): List<Sak>?

    @Throws(JournalforingFeilet::class)
    fun knyttBehandlingskjedeTilSak(fnr: String?, behandlingskjede: String?, sak: Sak, enhet: String?)
}
