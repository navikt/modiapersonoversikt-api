package no.nav.modiapersonoversikt.service.arbeidsfordeling

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.commondomain.Behandling
import no.nav.modiapersonoversikt.commondomain.parseV2BehandlingString
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain
import no.nav.modiapersonoversikt.consumer.pdl.generated.HentAdressebeskyttelse
import no.nav.modiapersonoversikt.consumer.pdl.generated.HentAdressebeskyttelse.AdressebeskyttelseGradering.*
import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface ArbeidsfordelingService {
    fun hentBehandlendeEnheter(
        fagomrade: String?,
        oppgavetype: String?,
        brukerIdent: Fnr?,
        underkategori: String?
    ): List<NorgDomain.Enhet>

    class ArbeidsfordelingException(message: String?, cause: Throwable?) : RuntimeException(message, cause)
}

class ArbeidsfordelingServiceImpl(
    private val norgApi: NorgApi,
    private val pdlOppslagService: PdlOppslagService,
    private val skjermedePersonerApi: SkjermedePersonerApi
) : ArbeidsfordelingService {
    val log: Logger = LoggerFactory.getLogger(ArbeidsfordelingService::class.java)

    override fun hentBehandlendeEnheter(
        fagomrade: String?,
        oppgavetype: String?,
        brukerIdent: Fnr?,
        underkategori: String?
    ): List<NorgDomain.Enhet> {
        return runCatching {
            val behandling: Behandling? = underkategori?.parseV2BehandlingString()
            val geografiskTilknyttning = brukerIdent?.get()?.let(pdlOppslagService::hentGeografiskTilknyttning)
            val diskresjonskode = brukerIdent?.get()
                ?.let(pdlOppslagService::hentAdressebeskyttelse)
                ?.let { if (it.isEmpty()) null else it.first().toNorgDiskresjonsKode() }

            val erEgenAnsatt = if (underkategori == "ANSOS_KNA") {
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
                diskresjonskode = diskresjonskode
            )
        }
            .getOrElse {
                log.error("Kunne ikke hente behandlende enheter", it)
                throw ArbeidsfordelingService.ArbeidsfordelingException(
                    "Kunne ikke hente behandlende enheter",
                    it
                )
            }
    }
}

private fun HentAdressebeskyttelse.Adressebeskyttelse.toNorgDiskresjonsKode(): NorgDomain.DiskresjonsKode {
    return when (this.gradering) {
        STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND -> NorgDomain.DiskresjonsKode.SPSF
        FORTROLIG -> NorgDomain.DiskresjonsKode.SPFO
        UGRADERT, __UNKNOWN_VALUE -> NorgDomain.DiskresjonsKode.ANY
    }
}
