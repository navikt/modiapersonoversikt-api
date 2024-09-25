package no.nav.modiapersonoversikt.service.journalforingsaker.kilder

import no.nav.modiapersonoversikt.consumer.arenainfotrygdproxy.ArenaInfotrygdApi
import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import no.nav.modiapersonoversikt.service.journalforingsaker.SakerKilde

internal class ArenaSakerV2(
    private val arenaInfotrygdApi: ArenaInfotrygdApi,
) : SakerKilde {
    override val kildeNavn: String
        get() = "ARENA"

    override fun leggTilSaker(
        fnr: String,
        saker: MutableList<JournalforingSak>,
    ) {
        if (saker.none(JournalforingSak.IS_ARENA_OPPFOLGING::test)) {
            arenaInfotrygdApi.hentOppfolgingssakFraArena(fnr)?.let { saker.add(it) }
        }
    }
}
