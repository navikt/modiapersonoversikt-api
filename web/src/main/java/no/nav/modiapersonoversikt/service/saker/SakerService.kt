package no.nav.modiapersonoversikt.service.saker

interface SakerService {
    data class Resultat(val saker: MutableList<Sak> = mutableListOf(), val feiledeSystemer: MutableList<String> = mutableListOf())

    fun hentSaker(fnr: String): Resultat

    fun hentSafSaker(fnr: String): Resultat
}
