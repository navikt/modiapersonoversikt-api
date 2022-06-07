package no.nav.modiapersonoversikt.service.saker.kilder

import no.nav.arena.services.lib.sakvedtak.SaksInfoListe
import no.nav.arena.services.sakvedtakservice.Bruker
import no.nav.arena.services.sakvedtakservice.FaultFeilIInputMsg
import no.nav.arena.services.sakvedtakservice.FaultGeneriskMsg
import no.nav.arena.services.sakvedtakservice.HentSaksInfoListeRequestV2
import no.nav.arena.services.sakvedtakservice.SakVedtakPortType
import no.nav.modiapersonoversikt.service.saker.Sak
import no.nav.modiapersonoversikt.service.saker.SakerKilde
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import javax.naming.ServiceUnavailableException
import javax.xml.ws.Holder

internal class ArenaSakerV2(val arenaSakVedtakService: SakVedtakPortType) : SakerKilde {
    private val log = LoggerFactory.getLogger(ArenaSakerV2::class.java)
    override val kildeNavn: String
        get() = "ARENA"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        if (saker.none(Sak.IS_ARENA_OPPFOLGING::test)) {
            hentOppfolgingssakFraArena(fnr)
                ?.also { saker.add(it) }
        }
    }

    private fun hentOppfolgingssakFraArena(fnr: String): Sak? {
        val request = HentSaksInfoListeRequestV2()
            .withBruker(Bruker().withBrukertypeKode("PERSON").withBrukerId(fnr))
            .withTema("OPP")

        val saksInfoListe = SaksInfoListe()
        val bruker = Holder(request.bruker)
        val saker: Holder<SaksInfoListe> = Holder(saksInfoListe)
        try {
            arenaSakVedtakService.hentSaksInfoListeV2(
                bruker,
                request.saksId,
                request.fomDato,
                request.tomDato,
                request.tema,
                request.isLukket,
                saker
            )
        } catch (e: FaultFeilIInputMsg) {
            log.error("Feil input til hentSaksInfoV2. FaultInfo: ${e.faultInfo}", e)
            throw ServiceUnavailableException(e.message)
        } catch (e: FaultGeneriskMsg) {
            log.error("Feil ved hentSaksInfoV2. FaultInfo: ${e.faultInfo}", e)
            throw ServiceUnavailableException(e.message)
        } catch (e: Exception) {
            log.error("Ukjent ved under kall på hentSaksInfoV2: ${e.message} ${e.cause}", e)
        }

        return saker
            .value
            .saksInfo
            .firstOrNull()
            ?.let(TIL_SAK)
    }

    companion object {
        private val TIL_SAK = { arenaSak: no.nav.arena.services.lib.sakvedtak.SaksInfo ->
            Sak().apply {
                saksId = arenaSak.saksId
                fagsystemSaksId = arenaSak.saksId
                fagsystemKode = Sak.FAGSYSTEMKODE_ARENA
                sakstype = Sak.SAKSTYPE_MED_FAGSAK
                temaKode = arenaSak.tema
                opprettetDato = DateTime(arenaSak.sakOpprettet.toGregorianCalendar().time)
                finnesIGsak = false
            }
        }
    }
}
