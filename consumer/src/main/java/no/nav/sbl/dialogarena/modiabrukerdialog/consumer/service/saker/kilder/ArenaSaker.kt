package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerKilde
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSBruker
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest

internal class ArenaSaker(val arbeidOgAktivitet: ArbeidOgAktivitet) : SakerKilde {
    override val kildeNavn: String
        get() = "ARENA"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        if (saker.none(Sak.IS_ARENA_OPPFOLGING::test)) {
            hentOppfolgingssakFraArena(fnr)
                    ?.also { saker.add(it) }
        }
    }

    private fun hentOppfolgingssakFraArena(fnr: String): Sak? {
        val request = WSHentSakListeRequest()
                .withBruker(WSBruker().withBrukertypeKode("PERSON").withBruker(fnr))
                .withFagomradeKode("OPP")

        return arbeidOgAktivitet.hentSakListe(request)
                .sakListe
                .firstOrNull()
                ?.let(TIL_SAK)
    }

    companion object {
        private val TIL_SAK = { arenaSak: no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak ->
            Sak().apply {
                saksId = arenaSak.saksId
                fagsystemSaksId = arenaSak.saksId
                fagsystemKode = Sak.FAGSYSTEMKODE_ARENA
                sakstype = Sak.SAKSTYPE_MED_FAGSAK
                temaKode = arenaSak.fagomradeKode.kode
                opprettetDato = arenaSak.endringsInfo.opprettetDato.toDateTimeAtStartOfDay()
                finnesIGsak = false
            }
        }
    }
}
