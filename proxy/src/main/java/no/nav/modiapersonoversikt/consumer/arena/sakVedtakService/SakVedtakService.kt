package no.nav.modiapersonoversikt.consumer.arena.sakVedtakService

interface SakVedtakService {
    fun hentOppfolgingssakFraArena(fnr: String): JournalforingSak?
}
