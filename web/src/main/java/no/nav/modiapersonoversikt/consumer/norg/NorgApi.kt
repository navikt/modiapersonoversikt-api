package no.nav.modiapersonoversikt.consumer.norg

import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain.EnhetGeografiskTilknyttning
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain.EnhetStatus
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain.OppgaveBehandlerFilter
import no.nav.modiapersonoversikt.legacy.api.domain.norg.generated.apis.ArbeidsfordelingApi
import no.nav.modiapersonoversikt.legacy.api.domain.norg.generated.apis.EnhetApi
import no.nav.modiapersonoversikt.legacy.api.domain.norg.generated.models.RsArbeidsFordelingCriteriaSkjermetDTO
import no.nav.modiapersonoversikt.legacy.api.domain.norg.generated.models.RsEnhetDTO
import no.nav.modiapersonoversikt.legacy.api.domain.norg.generated.models.RsNavKontorDTO
import no.nav.modiapersonoversikt.service.kodeverksmapper.domain.Behandling
import okhttp3.OkHttpClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface NorgApi {
    companion object {
        @JvmStatic
        val IKKE_NEDLAGT: List<EnhetStatus> = EnhetStatus.values().asList().minus(EnhetStatus.NEDLAGT)
    }

    fun hentGeografiskTilknyttning(enhet: EnhetId): List<EnhetGeografiskTilknyttning>

    fun hentEnheter(
        enhetId: String?,
        oppgaveBehandlende: OppgaveBehandlerFilter = OppgaveBehandlerFilter.UFILTRERT,
        enhetStatuser: List<EnhetStatus> = IKKE_NEDLAGT
    ): List<NorgDomain.Enhet>

    fun finnNavKontor(geografiskTilknytning: String, diskresjonskode: NorgDomain.DiskresjonsKode?): NorgDomain.Enhet

    fun hentBehandlendeEnheter(
        behandling: Behandling?,
        geografiskTilknyttning: String?,
        oppgavetype: String?,
        fagomrade: String?,
        erEgenAnsatt: Boolean?,
        diskresjonskode: String?
    ): List<NorgDomain.Enhet>
}

class NorgApiImpl(url: String, httpClient: OkHttpClient) : NorgApi {
    private val log: Logger = LoggerFactory.getLogger(NorgApi::class.java)
    private val arbeidsfordelingApi = ArbeidsfordelingApi(url, httpClient)
    private val enhetApi = EnhetApi(url, httpClient)

    override fun hentGeografiskTilknyttning(enhet: EnhetId): List<EnhetGeografiskTilknyttning> {
        return enhetApi
            .getNavKontorerByEnhetsnummerUsingGET(enhet.get())
            .map(::toInternalDomain)
    }

    override fun hentEnheter(
        enhetId: String?,
        oppgaveBehandlende: OppgaveBehandlerFilter,
        enhetStatuser: List<EnhetStatus>
    ): List<NorgDomain.Enhet> {
        return enhetApi
            .getAllEnheterUsingGET(
                enhetStatusListe = enhetStatuser.map { it.name },
                enhetsnummerListe = enhetId?.let { listOf(it) } ?: emptyList(),
                oppgavebehandlerFilter = oppgaveBehandlende.name
            )
            .map(::toInternalDomain)
    }

    override fun finnNavKontor(geografiskTilknytning: String, diskresjonskode: NorgDomain.DiskresjonsKode?): NorgDomain.Enhet {
        return enhetApi.getEnhetByGeografiskOmraadeUsingGET(
            geografiskOmraade = geografiskTilknytning,
            disk = diskresjonskode?.name
        ).let(::toInternalDomain)
    }

    override fun hentBehandlendeEnheter(
        behandling: Behandling?,
        geografiskTilknyttning: String?,
        oppgavetype: String?,
        fagomrade: String?,
        erEgenAnsatt: Boolean?,
        diskresjonskode: String?
    ): List<NorgDomain.Enhet> {
        return arbeidsfordelingApi
            .runCatching {
                getBehandlendeEnheterUsingPOST(
                    RsArbeidsFordelingCriteriaSkjermetDTO(
                        oppgavetype = oppgavetype,
                        tema = fagomrade,
                        skjermet = erEgenAnsatt,
                        behandlingstema = behandling?.behandlingstema,
                        behandlingstype = behandling?.behandlingstype,
                        geografiskOmraade = geografiskTilknyttning ?: "",
                        diskresjonskode = diskresjonskode,
                        enhetNummer = null,
                        temagruppe = null,
                    )
                )
            }
            .getOrElse {
                log.error("Kunne ikke hente enheter fra arbeidsfordeling.", it)
                emptyList()
            }
            .map(::toInternalDomain)
    }

    private fun toInternalDomain(kontor: RsNavKontorDTO): NorgDomain.EnhetGeografiskTilknyttning = EnhetGeografiskTilknyttning(
        alternativEnhetId = kontor.alternativEnhetId?.toString(),
        enhetId = kontor.enhetId?.toString(),
        geografiskOmraade = kontor.geografiskOmraade,
        navKontorId = kontor.navKontorId?.toString()
    )

    private fun toInternalDomain(enhet: RsEnhetDTO): NorgDomain.Enhet = NorgDomain.Enhet(
        enhetId = enhet.enhetNr,
        enhetNavn = enhet.navn,
        status = enhet.status?.let(EnhetStatus::valueOf)
    )
}
