package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerKilde
import no.nav.tjeneste.virksomhet.behandlesak.v1.BehandleSakV1
import no.nav.tjeneste.virksomhet.behandlesak.v1.OpprettSakSakEksistererAllerede
import no.nav.tjeneste.virksomhet.behandlesak.v1.OpprettSakUgyldigInput
import no.nav.tjeneste.virksomhet.behandlesak.v1.informasjon.WSFagomraader
import no.nav.tjeneste.virksomhet.behandlesak.v1.informasjon.WSFagsystemer
import no.nav.tjeneste.virksomhet.behandlesak.v1.informasjon.WSSakstyper
import no.nav.tjeneste.virksomhet.behandlesak.v1.meldinger.WSOpprettSakRequest
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakForMangeForekomster
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakUgyldigInput
import no.nav.tjeneste.virksomhet.sak.v1.SakV1
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSPerson
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakRequest
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors

class GsakSaker(val sakV1: SakV1, val behandleSakWS: BehandleSakV1) : SakerKilde {
    override val kildeNavn: String
        get() = "GSAK"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        val response = sakV1.finnSak(
                WSFinnSakRequest()
                        .withBruker(WSPerson().withIdent(fnr)))
        val gsakSaker = response.sakListe.stream().map(TIL_SAK).collect(Collectors.toList())
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
            finnSakIdFraGsak(fnr, sak)
                    .orElseThrow { RuntimeException("Fant ikke sak", opprettSakException) }
        } catch (opprettSakException: OpprettSakUgyldigInput) {
            throw RuntimeException(opprettSakException)
        }
    }

    private fun finnSakIdFraGsak(fnr: String, sak: Sak): Optional<String> {
        return try {
            val request = WSFinnSakRequest()
                    .withBruker(WSPerson().withIdent(fnr))
                    .withFagomraadeListe(no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSFagomraader().withValue(sak.temaKode))
                    .withFagsystem(no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSFagsystemer().withValue(sak.fagsystemKode))
                    .withFagsystemSakId(sak.saksId)
            sakV1
                    .finnSak(request)
                    .sakListe
                    .stream()
                    .filter { finnSak: WSSak -> finnSak.sakstype.value == sak.sakstype }
                    .findFirst()
                    .map { it.sakId }
        } catch (finnSakException: FinnSakForMangeForekomster) {
            throw RuntimeException(finnSakException)
        } catch (finnSakException: FinnSakUgyldigInput) {
            throw RuntimeException(finnSakException)
        }
    }

    companion object {
        private const val VEDTAKSLOSNINGEN = "FS36"

        @JvmField
        val TIL_SAK = Function { wsSak: WSSak ->
            val sak = Sak()
            sak.opprettetDato = wsSak.opprettelsetidspunkt
            sak.saksId = wsSak.sakId
            sak.fagsystemSaksId = getFagsystemSakId(wsSak)
            sak.temaKode = wsSak.fagomraade.value
            sak.sakstype = getSakstype(wsSak)
            sak.fagsystemKode = wsSak.fagsystem.value
            sak.finnesIGsak = true
            sak
        }

        private fun getSakstype(wsSak: WSSak): String {
            return if (VEDTAKSLOSNINGEN == wsSak.fagsystem.value) Sak.SAKSTYPE_MED_FAGSAK else wsSak.sakstype.value
        }

        private fun getFagsystemSakId(wsSak: WSSak): String {
            return if (VEDTAKSLOSNINGEN == wsSak.fagsystem.value) wsSak.sakId else wsSak.fagsystemSakId
        }
    }
}
