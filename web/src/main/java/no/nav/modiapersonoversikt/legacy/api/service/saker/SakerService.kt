package no.nav.modiapersonoversikt.legacy.api.service.saker

import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak

interface SakerService {
    class Resultat(val saker: MutableList<Sak> = mutableListOf(), val feiledeSystemer: MutableList<String> = mutableListOf())

    fun hentSaker(fnr: String): Resultat

    fun hentSakSaker(fnr: String): Resultat
}
