package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerKilde
import no.nav.tjeneste.virksomhet.behandlesak.v1.BehandleSakV1
import no.nav.tjeneste.virksomhet.behandlesak.v1.OpprettSakSakEksistererAllerede
import no.nav.tjeneste.virksomhet.behandlesak.v1.informasjon.WSFagomraader
import no.nav.tjeneste.virksomhet.behandlesak.v1.informasjon.WSFagsystemer
import no.nav.tjeneste.virksomhet.behandlesak.v1.informasjon.WSSakstyper
import no.nav.tjeneste.virksomhet.behandlesak.v1.meldinger.WSOpprettSakRequest
import no.nav.tjeneste.virksomhet.sak.v1.SakV1
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSPerson
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakRequest

internal class GsakSaker(val sakV1: SakV1, val behandleSakWS: BehandleSakV1) : SakerKilde {
    override val kildeNavn: String
        get() = "GSAK"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        val response = sakV1.finnSak(
                WSFinnSakRequest()
                        .withBruker(WSPerson().withIdent(fnr)))
        val gsakSaker = response.sakListe.map(TIL_SAK)
        saker.addAll(gsakSaker)
    }

    fun opprettSak(fnr: String, sak: Sak): String {
        return try {
            val request = WSOpprettSakRequest().withSak(
                    no.nav.tjeneste.virksomhet.behandlesak.v1.informasjon.WSSak()
                            .withGjelderBrukerListe(no.nav.tjeneste.virksomhet.behandlesak.v1.informasjon.WSPerson().withIdent(fnr))
                            .withFagomraade(WSFagomraader().withValue(sak.temaKode))
                            .withFagsystem(WSFagsystemer().withValue(sak.fagsystemKode))
                            .withFagsystemSakId(sak.saksId)
                            .withSakstype(WSSakstyper().withValue(sak.sakstype)))
            behandleSakWS.opprettSak(request).sakId
        } catch (opprettSakException: OpprettSakSakEksistererAllerede) {
            finnSakIdFraGsak(fnr, sak) ?: throw RuntimeException("Fant ikke sak", opprettSakException)
        }
    }

    private fun finnSakIdFraGsak(fnr: String, sak: Sak): String? {
        val request = WSFinnSakRequest()
                .withBruker(WSPerson().withIdent(fnr))
                .withFagomraadeListe(no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSFagomraader().withValue(sak.temaKode))
                .withFagsystem(no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSFagsystemer().withValue(sak.fagsystemKode))
                .withFagsystemSakId(sak.saksId)

        return sakV1
                .finnSak(request)
                .sakListe
                .firstOrNull { wsSak: WSSak -> wsSak.sakstype.value == sak.sakstype }
                ?.sakId
    }

    companion object {
        private const val VEDTAKSLOSNINGEN = "FS36"

        @JvmField
        val TIL_SAK = { wsSak: WSSak ->
            Sak().apply {
                opprettetDato = wsSak.opprettelsetidspunkt
                saksId = wsSak.sakId
                fagsystemSaksId = getFagsystemSakId(wsSak)
                temaKode = wsSak.fagomraade?.value
                sakstype = getSakstype(wsSak)
                fagsystemKode = wsSak.fagsystem?.value
                finnesIGsak = true
            }
        }

        private fun getSakstype(wsSak: WSSak): String? {
            return if (VEDTAKSLOSNINGEN == wsSak.fagsystem?.value) Sak.SAKSTYPE_MED_FAGSAK else wsSak.sakstype?.value
        }

        private fun getFagsystemSakId(wsSak: WSSak): String? {
            return if (VEDTAKSLOSNINGEN == wsSak.fagsystem?.value) wsSak.sakId else wsSak.fagsystemSakId
        }
    }
}
