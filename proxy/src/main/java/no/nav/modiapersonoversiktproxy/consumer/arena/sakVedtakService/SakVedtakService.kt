package no.nav.modiapersonoversiktproxy.consumer.arena.sakVedtakService

interface SakVedtakService {
    fun hentOppfolgingssakFraArena(fnr: String): JournalforingSak?
}
