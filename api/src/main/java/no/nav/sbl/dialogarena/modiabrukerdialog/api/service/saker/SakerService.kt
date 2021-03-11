package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saker

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.saker.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet

interface SakerService {
    class Resultat(val saker: MutableList<Sak> = mutableListOf(), val feiledeSystemer: MutableList<String> = mutableListOf())

    fun hentSaker(fnr: String): Resultat

    @Deprecated("")
    fun hentSammensatteSaker(fnr: String): List<Sak>

    @Deprecated("")
    fun hentPensjonSaker(fnr: String): List<Sak>

    @Throws(JournalforingFeilet::class)
    fun knyttBehandlingskjedeTilSak(fnr: String?, behandlingskjede: String?, sak: Sak, enhet: String?)
}
