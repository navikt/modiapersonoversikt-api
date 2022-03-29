package no.nav.modiapersonoversikt.service.arbeidsfordeling

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.commondomain.Behandling
import no.nav.modiapersonoversikt.commondomain.parseV2BehandlingString
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain
import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import no.nav.modiapersonoversikt.rest.persondata.PersondataService
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
    private val persondataService: PersondataService,
    private val skjermedePersonerApi: SkjermedePersonerApi
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
                brukerIdent?.get()?.let(skjermedePersonerApi::erSkjermetPerson)
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
