package no.nav.modiapersonoversikt.service.arbeidsfordeling

import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain.EnhetGeografiskTilknyttning
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.egenansatt.EgenAnsattService
import no.nav.modiapersonoversikt.rest.persondata.PersondataService
import no.nav.modiapersonoversikt.service.kodeverksmapper.domain.Behandling
import no.nav.modiapersonoversikt.service.kodeverksmapper.domain.parseV2BehandlingString
import org.slf4j.LoggerFactory

interface ArbeidsfordelingService {
    fun hentBehandlendeEnheter(
        fagomrade: String?,
        oppgavetype: String?,
        brukerIdent: Fnr?,
        underkategori: String?
    ): List<NorgDomain.Enhet>

    fun hentGeografiskTilknyttning(valgtEnhet: String): List<EnhetGeografiskTilknyttning>

    class ArbeidsfordelingException(message: String?, cause: Throwable?) : RuntimeException(message, cause)
}

class ArbeidsfordelingServiceImpl(
    private val norgApi: NorgApi,
    private val persondataService: PersondataService,
    private val egenAnsattService: EgenAnsattService
) : ArbeidsfordelingService {
    val log = LoggerFactory.getLogger(ArbeidsfordelingService::class.java)

    override fun hentBehandlendeEnheter(
        fagomrade: String?,
        oppgavetype: String?,
        brukerIdent: Fnr?,
        underkategori: String?
    ): List<NorgDomain.Enhet> {
        return runCatching {
            val behandling: Behandling? = underkategori?.parseV2BehandlingString()
            val geografiskTilknyttning = brukerIdent?.get()?.let(persondataService::hentGeografiskTilknytning)
            val diskresjonskode = brukerIdent?.get()
                ?.let(persondataService::hentAdressebeskyttelse)
                ?.let { if (it.isEmpty()) null else it.first().kode.name }

            val erEgenAnsatt = if (underkategori == "ANSOS_KNA") {
                false
            } else {
                brukerIdent?.get()?.let(egenAnsattService::erEgenAnsatt)
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

    override fun hentGeografiskTilknyttning(valgtEnhet: String): List<EnhetGeografiskTilknyttning> {
        return norgApi
            .runCatching {
                this.hentGeografiskTilknyttning(EnhetId.of(valgtEnhet))
            }
            .onFailure { log.error("Kunne ikke hente geografisk tilknyttning", it) }
            .getOrDefault(emptyList())
    }
}
