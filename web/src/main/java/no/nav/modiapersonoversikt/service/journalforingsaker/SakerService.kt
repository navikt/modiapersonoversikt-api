package no.nav.modiapersonoversikt.service.journalforingsaker

interface SakerService {
    data class Resultat(val saker: MutableList<JournalforingSak> = mutableListOf(), val feiledeSystemer: MutableList<String> = mutableListOf())

    fun hentSaker(fnr: String): Resultat

    fun hentSafSaker(fnr: String): Resultat
}
