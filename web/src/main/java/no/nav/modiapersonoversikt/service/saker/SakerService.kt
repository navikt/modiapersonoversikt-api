package no.nav.modiapersonoversikt.service.saker

interface SakerService {
    class Resultat(val saker: MutableList<Sak> = mutableListOf(), val feiledeSystemer: MutableList<String> = mutableListOf())

    fun hentSaker(fnr: String): Resultat

    fun hentSakSaker(fnr: String): Resultat
}
