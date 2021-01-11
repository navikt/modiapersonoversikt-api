package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerKilde
import org.joda.time.DateTime

internal class OppfolgingsSaker : SakerKilde {
    override val kildeNavn: String
        get() = "OPPFOLGING"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        val generelleSaker = saker.filter(Sak.IS_GENERELL_SAK::test)
        val fagsaker = saker.filter(Sak.IS_GENERELL_SAK.negate()::test)

        val oppfolgingssakFinnesIFagsaker = inneholderOppfolgingssak(fagsaker)
        val oppfolgingssakFinnesIGenerelleSaker = inneholderOppfolgingssak(generelleSaker)

        if (oppfolgingssakFinnesIFagsaker && oppfolgingssakFinnesIGenerelleSaker) {
            fjernGenerellOppfolgingssak(saker, generelleSaker)
        } else if (!oppfolgingssakFinnesIFagsaker && !oppfolgingssakFinnesIGenerelleSaker) {
            saker.add(lagGenerellSakMedTema(Sak.TEMAKODE_OPPFOLGING))
        }
    }

    private fun fjernGenerellOppfolgingssak(saker: MutableList<Sak>, generelleSaker: List<Sak>) {
        for (sak in generelleSaker) {
            if (Sak.TEMAKODE_OPPFOLGING == sak.temaKode) {
                saker.remove(sak)
            }
        }
    }

    companion object {
        private fun inneholderOppfolgingssak(saker: List<Sak>): Boolean {
            return saker.any { sak -> Sak.TEMAKODE_OPPFOLGING == sak.temaKode }
        }

        private fun lagGenerellSakMedTema(temakode: String): Sak {
            return Sak().apply {
                temaKode = temakode
                finnesIGsak = false
                fagsystemKode = Sak.FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK
                sakstype = Sak.SAKSTYPE_GENERELL
                opprettetDato = DateTime.now()
            }
        }
    }
}
