package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerKilde
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakApiGateway
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakDto
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
import org.joda.time.DateTime
import java.time.ZoneId
import java.time.ZonedDateTime


internal class GsakSaker(
        private val sakV1: SakV1,
        private val behandleSakWS: BehandleSakV1,
        private val sakApiGateway: SakApiGateway) : SakerKilde {

    override val kildeNavn: String
        get() = "GSAK"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        val response = sakApiGateway.hentSaker(fnr)
        val gsakSaker = response.map(TIL_SAK)
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
        private const val SAKSTYPE_GENERELL = "GEN"
        private const val SAKSTYPE_MED_FAGSAK = "MFS"


        @JvmField
        val TIL_SAK = { sakDto: SakDto ->
            Sak().apply {
                opprettetDato = sakDto.opprettetTidspunkt?.let { convertJavaDateTimeToJoda(it) }
                saksId = sakDto.id.toString()
                fagsystemSaksId = getFagsystemSakId(sakDto)
                temaKode = sakDto.tema
                fagsystemKode = sakDto.applikasjon
                finnesIGsak = true
                sakstype = getSakstype(sakDto)
            }

        }

        private fun getSakstype(sakDto: SakDto): String? {

            return when (sakDto.applikasjon) {
                VEDTAKSLOSNINGEN -> SAKSTYPE_MED_FAGSAK
                else -> {
                    if (sakDto.fagsakNr != null)
                        SAKSTYPE_MED_FAGSAK
                    else
                        SAKSTYPE_GENERELL
                }
            }
        }

        private fun getFagsystemSakId(sakDto: SakDto): String? {
            return if (VEDTAKSLOSNINGEN == sakDto.applikasjon) sakDto.id.toString() else sakDto.fagsakNr
        }

        private fun convertJavaDateTimeToJoda(dateTime: java.time.LocalDateTime): DateTime {
            val zdt: ZonedDateTime = dateTime.atZone(ZoneId.systemDefault())
            return DateTime(zdt.toInstant().toEpochMilli())
        }
    }

}
