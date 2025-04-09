package no.nav.modiapersonoversikt.rest.arena

import no.nav.modiapersonoversikt.consumer.arena.sakVedtakService.JournalforingSak
import no.nav.modiapersonoversikt.consumer.arena.sakVedtakService.SakVedtakService
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
