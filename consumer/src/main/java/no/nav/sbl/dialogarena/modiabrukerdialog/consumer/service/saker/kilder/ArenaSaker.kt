package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerKilde
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSBruker
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest
import java.util.*
import java.util.function.Function

class ArenaSaker(val arbeidOgAktivitet: ArbeidOgAktivitet) : SakerKilde {
    override val kildeNavn: String
        get() = "ARENA"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        if (saker.stream().noneMatch(Sak.IS_ARENA_OPPFOLGING)) {
            val oppfolging = hentOppfolgingssakFraArena(fnr)
            oppfolging.ifPresent { e: Sak -> saker.add(e) }
        }
    }

    private fun hentOppfolgingssakFraArena(fnr: String): Optional<Sak> {
        val request = WSHentSakListeRequest()
                .withBruker(WSBruker().withBrukertypeKode("PERSON").withBruker(fnr))
                .withFagomradeKode("OPP")
        return arbeidOgAktivitet!!.hentSakListe(request)
                .sakListe
                .stream()
                .findFirst()
                .map(TIL_SAK)
    }

    companion object {
        private val TIL_SAK = Function { arenaSak: no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak ->
            val sak = Sak()
            sak.saksId = arenaSak.saksId
            sak.fagsystemSaksId = arenaSak.saksId
            sak.fagsystemKode = Sak.FAGSYSTEMKODE_ARENA
            sak.sakstype = Sak.SAKSTYPE_MED_FAGSAK
            sak.temaKode = arenaSak.fagomradeKode.kode
            sak.opprettetDato = arenaSak.endringsInfo.opprettetDato.toDateTimeAtStartOfDay()
            sak.finnesIGsak = false
            sak
        }
    }
}
