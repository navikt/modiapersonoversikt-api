package no.nav.modiapersonoversikt.legacy.api.service

interface HenvendelseLesService {
    fun alleBehandlingsIderTilhorerBruker(fnr: String, behandlingsIder: List<String>): Boolean
}
