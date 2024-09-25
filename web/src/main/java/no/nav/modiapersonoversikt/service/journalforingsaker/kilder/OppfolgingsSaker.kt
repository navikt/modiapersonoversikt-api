package no.nav.modiapersonoversikt.service.journalforingsaker.kilder

import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import no.nav.modiapersonoversikt.service.journalforingsaker.SakerKilde
import org.joda.time.DateTime

internal class OppfolgingsSaker : SakerKilde {
    override val kildeNavn: String
        get() = "OPPFOLGING"

    override fun leggTilSaker(
        fnr: String,
        saker: MutableList<JournalforingSak>,
    ) {
        val generelleSaker = saker.filter(JournalforingSak.IS_GENERELL_SAK::test)
        val fagsaker = saker.filter(JournalforingSak.IS_GENERELL_SAK.negate()::test)

        val oppfolgingssakFinnesIFagsaker = inneholderOppfolgingssak(fagsaker)
        val oppfolgingssakFinnesIGenerelleSaker = inneholderOppfolgingssak(generelleSaker)

        if (oppfolgingssakFinnesIFagsaker && oppfolgingssakFinnesIGenerelleSaker) {
            fjernGenerellOppfolgingssak(saker, generelleSaker)
        } else if (!oppfolgingssakFinnesIFagsaker && !oppfolgingssakFinnesIGenerelleSaker) {
            saker.add(
                JournalforingSak().apply {
                    temaKode = JournalforingSak.TEMAKODE_OPPFOLGING
                    finnesIGsak = false
                    fagsystemKode = JournalforingSak.FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK
                    sakstype = JournalforingSak.SAKSTYPE_GENERELL
                    opprettetDato = DateTime.now()
                },
            )
        }
    }

    private fun fjernGenerellOppfolgingssak(
        saker: MutableList<JournalforingSak>,
        generelleSaker: List<JournalforingSak>,
    ) {
        for (sak in generelleSaker) {
            if (JournalforingSak.TEMAKODE_OPPFOLGING == sak.temaKode) {
                saker.remove(sak)
            }
        }
    }

    companion object {
        private fun inneholderOppfolgingssak(saker: List<JournalforingSak>): Boolean =
            saker.any { sak -> JournalforingSak.TEMAKODE_OPPFOLGING == sak.temaKode }
    }
}
