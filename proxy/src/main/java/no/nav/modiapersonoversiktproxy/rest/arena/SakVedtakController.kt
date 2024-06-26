package no.nav.modiapersonoversiktproxy.rest.arena

import no.nav.modiapersonoversiktproxy.consumer.arena.sakVedtakService.JournalforingSak
import no.nav.modiapersonoversiktproxy.consumer.arena.sakVedtakService.SakVedtakService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest")
class SakVedtakController
    @Autowired
    constructor(
        private val sakVedtakService: SakVedtakService,
    ) {
        @PostMapping("/sakvedtak")
        fun hentOppfolgingssakFraArena(
            @RequestBody fnr: String,
        ): JournalforingSak? = sakVedtakService.hentOppfolgingssakFraArena(fnr)
    }
