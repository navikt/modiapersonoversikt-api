package no.nav.modiapersonoversiktproxy.consumer.arena.sakVedtakService

import jakarta.xml.ws.Holder
import no.nav.arena.services.lib.sakvedtak.SaksInfoListe
import no.nav.arena.services.sakvedtakservice.Bruker
import no.nav.arena.services.sakvedtakservice.FaultFeilIInputMsg
import no.nav.arena.services.sakvedtakservice.FaultGeneriskMsg
import no.nav.arena.services.sakvedtakservice.HentSaksInfoListeRequestV2
import no.nav.arena.services.sakvedtakservice.SakVedtakPortType
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import javax.naming.ServiceUnavailableException

class SakVedtakServiceImpl(private val arenaSakVedtakService: SakVedtakPortType) : SakVedtakService {
    private val log = LoggerFactory.getLogger(SakVedtakServiceImpl::class.java)

    override fun hentOppfolgingssakFraArena(fnr: String): JournalforingSak? {
        val request =
            HentSaksInfoListeRequestV2()
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
                saker,
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

        val response = saker
            .value
            .saksInfo
            .firstOrNull()
            ?.let(TIL_SAK)

        log.debug("Response: $response")

        return response
    }

    companion object {
        private val TIL_SAK = { arenaSak: no.nav.arena.services.lib.sakvedtak.SaksInfo ->
            JournalforingSak().apply {
                saksId = arenaSak.saksId
                fagsystemSaksId = arenaSak.saksId
                fagsystemKode = JournalforingSak.FAGSYSTEMKODE_ARENA
                sakstype = JournalforingSak.SAKSTYPE_MED_FAGSAK
                temaKode = arenaSak.tema
                opprettetDato = DateTime(arenaSak.sakOpprettet.toGregorianCalendar().time)
                finnesIGsak = false
            }
        }
    }
}
