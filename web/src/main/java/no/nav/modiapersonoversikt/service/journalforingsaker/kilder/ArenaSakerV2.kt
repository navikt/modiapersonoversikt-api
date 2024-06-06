package no.nav.modiapersonoversikt.service.journalforingsaker.kilder

import no.nav.modiapersonoversikt.consumer.arenainfotrygdproxy.ArenaInfotrygdApi
import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import no.nav.modiapersonoversikt.service.journalforingsaker.SakerKilde
import org.slf4j.LoggerFactory

internal class ArenaSakerV2(private val arenaInfotrygdApi: ArenaInfotrygdApi) : SakerKilde {
    private val log = LoggerFactory.getLogger(ArenaSakerV2::class.java)
    override val kildeNavn: String
        get() = "ARENA"

    override fun leggTilSaker(
        fnr: String,
        saker: MutableList<JournalforingSak>,
    ) {
        if (saker.none(JournalforingSak.IS_ARENA_OPPFOLGING::test)) {
            arenaInfotrygdApi.hentOppfolgingssakFraArena(fnr)
                ?.also { saker.add(it) }
        }
    }
}
