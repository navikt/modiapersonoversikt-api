package no.nav.sbl.dialogarena.modiabrukerdialog.api.service

interface HenvendelseLesService {
    fun alleBehandlingsIderTilhorerBruker(fnr: String, behandlingsIder: List<String>): Boolean
    fun alleHenvendelseIderTilhorerBruker(fnr: String, henvendelseIder: List<String>): Boolean
}
