package no.nav.modiapersonoversikt.service.arbeidsfordeling

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.commondomain.Behandling
import no.nav.modiapersonoversikt.commondomain.parseV2BehandlingString
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain
import no.nav.modiapersonoversikt.consumer.pdl.generated.enums.AdressebeskyttelseGradering.*
import no.nav.modiapersonoversikt.consumer.pdlPip.PdlPipApi
import no.nav.modiapersonoversikt.consumer.pdlPipApi.generated.models.PipAdressebeskyttelse
import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface ArbeidsfordelingService {
    fun hentBehandlendeEnheter(
        fagomrade: String?,
        oppgavetype: String?,
        brukerIdent: Fnr?,
        underkategori: String?,
    ): List<NorgDomain.Enhet>

    class ArbeidsfordelingException(message: String?, cause: Throwable?) : RuntimeException(message, cause)
}

class ArbeidsfordelingServiceImpl(
    private val norgApi: NorgApi,
    private val pdlPip: PdlPipApi,
    private val skjermedePersonerApi: SkjermedePersonerApi,
) : ArbeidsfordelingService {
    val log: Logger = LoggerFactory.getLogger(ArbeidsfordelingService::class.java)

    override fun hentBehandlendeEnheter(
        fagomrade: String?,
        oppgavetype: String?,
        brukerIdent: Fnr?,
        underkategori: String?,
    ): List<NorgDomain.Enhet> {
        return runCatching {
            val behandling: Behandling? = underkategori?.parseV2BehandlingString()
            val geografiskTilknyttning =
                brukerIdent?.get()?.let(pdlPip::hentGeografiskTilknytning)?.run {
                    gtBydel ?: gtKommune ?: gtLand
                }
            val diskresjonskode =
                brukerIdent?.get()
                    ?.let(pdlPip::hentAdresseBeskyttelse)
                    ?.let { if (it.isEmpty()) null else it.first().toNorgDiskresjonsKode() }

            val erEgenAnsatt =
                if (underkategori == "ANSOS_KNA") {
                    false
                } else {
                    brukerIdent?.let(skjermedePersonerApi::erSkjermetPerson)
                }

            norgApi.hentBehandlendeEnheter(
                behandling = behandling,
                geografiskTilknyttning = geografiskTilknyttning,
                oppgavetype = oppgavetype,
                fagomrade = fagomrade,
                erEgenAnsatt = erEgenAnsatt,
                diskresjonskode = diskresjonskode,
            )
        }
            .getOrElse {
                log.error("Kunne ikke hente behandlende enheter", it)
                throw ArbeidsfordelingService.ArbeidsfordelingException(
                    "Kunne ikke hente behandlende enheter",
                    it,
                )
            }
    }
}

private fun PipAdressebeskyttelse.toNorgDiskresjonsKode(): NorgDomain.DiskresjonsKode {
    return when (this.gradering) {
        STRENGT_FORTROLIG.toString(), STRENGT_FORTROLIG_UTLAND.toString() -> NorgDomain.DiskresjonsKode.SPSF
        FORTROLIG.toString() -> NorgDomain.DiskresjonsKode.SPFO
        else -> NorgDomain.DiskresjonsKode.ANY
    }
}
