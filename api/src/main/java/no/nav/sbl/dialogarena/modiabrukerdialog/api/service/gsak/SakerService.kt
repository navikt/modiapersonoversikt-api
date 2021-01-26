package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet


interface SakerService {
    class Resultat(val saker: ArrayList<Sak> = ArrayList(), val feiledeSystemer: ArrayList<String?> = ArrayList())

    fun hentSaker(fnr: String): Resultat

    @Deprecated("")
    fun hentSammensatteSaker(fnr: String): List<Sak>

    @Deprecated("")
    fun hentPensjonSaker(fnr: String): List<Sak>

    @Throws(JournalforingFeilet::class)
    fun knyttBehandlingskjedeTilSak(fnr: String?, behandlingskjede: String?, sak: Sak, enhet: String?)
}
