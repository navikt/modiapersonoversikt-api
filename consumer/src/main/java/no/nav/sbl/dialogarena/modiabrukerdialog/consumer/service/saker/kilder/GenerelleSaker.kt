package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerKilde
import org.joda.time.DateTime
import java.util.stream.Collectors

class GenerelleSaker : SakerKilde {
    override val kildeNavn: String
        get() = "GENERELLE"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        val generelleSaker = saker.stream()
                .filter { obj: Sak -> obj.isSakstypeForVisningGenerell }
                .collect(Collectors.toList())
        saker.addAll(Sak.GODKJENTE_TEMA_FOR_GENERELL_SAK.stream()
                .filter { temakode: String -> harIngenSakerMedTemakode(temakode, generelleSaker) && Sak.TEMAKODE_OPPFOLGING != temakode }
                .map { temakode: String -> lagGenerellSakMedTema(temakode) }
                .collect(Collectors.toList()))
    }

    companion object {
        private fun harIngenSakerMedTemakode(temakode: String, generelleSaker: List<Sak>): Boolean {
            return generelleSaker.stream().noneMatch { sak: Sak -> temakode == sak.temaKode }
        }

        private fun lagGenerellSakMedTema(temakode: String): Sak {
            val sak = Sak()
            sak.temaKode = temakode
            sak.finnesIGsak = false
            sak.fagsystemKode = Sak.FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK
            sak.sakstype = Sak.SAKSTYPE_GENERELL
            sak.opprettetDato = DateTime.now()
            return sak
        }
    }
}
