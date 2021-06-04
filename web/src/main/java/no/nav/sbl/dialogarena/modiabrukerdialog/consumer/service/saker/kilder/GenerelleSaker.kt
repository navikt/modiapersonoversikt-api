package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.saker.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerKilde
import org.joda.time.DateTime

internal class GenerelleSaker : SakerKilde {
    override val kildeNavn: String
        get() = "GENERELLE"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        val generelleSaker = saker
            .filter { obj: Sak -> obj.isSakstypeForVisningGenerell }

        val manglendeGenerelleSaker = Sak.GODKJENTE_TEMA_FOR_GENERELL_SAK
            .filter { temakode: String -> harIngenSakerMedTemakode(temakode, generelleSaker) && Sak.TEMAKODE_OPPFOLGING != temakode }
            .map { temakode: String -> lagGenerellSakMedTema(temakode) }

        saker.addAll(manglendeGenerelleSaker)
    }

    companion object {
        private fun harIngenSakerMedTemakode(temakode: String, generelleSaker: List<Sak>): Boolean {
            return generelleSaker.none { it.temaKode == temakode }
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
