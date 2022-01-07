package no.nav.modiapersonoversikt.consumer.norg

import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain.Enhet
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain.EnhetGeografiskTilknyttning
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain.EnhetKontaktinformasjon
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain.EnhetStatus
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain.OppgaveBehandlerFilter
import no.nav.modiapersonoversikt.legacy.api.domain.norg.generated.apis.ArbeidsfordelingApi
import no.nav.modiapersonoversikt.legacy.api.domain.norg.generated.apis.EnhetApi
import no.nav.modiapersonoversikt.legacy.api.domain.norg.generated.apis.KontaktinformasjonApi
import no.nav.modiapersonoversikt.legacy.api.domain.norg.generated.models.*
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
    ): List<Enhet>

    fun finnNavKontor(geografiskTilknytning: String, diskresjonskode: NorgDomain.DiskresjonsKode?): Enhet

    fun hentBehandlendeEnheter(
        behandling: Behandling?,
        geografiskTilknyttning: String?,
        oppgavetype: String?,
        fagomrade: String?,
        erEgenAnsatt: Boolean?,
        diskresjonskode: String?
    ): List<Enhet>

    fun hentKontaktinfo(enhet: String): EnhetKontaktinformasjon
}

class NorgApiImpl(url: String, httpClient: OkHttpClient) : NorgApi {
    private val log: Logger = LoggerFactory.getLogger(NorgApi::class.java)
    private val arbeidsfordelingApi = ArbeidsfordelingApi(url, httpClient)
    private val enhetApi = EnhetApi(url, httpClient)
    private val enhetKontaktinfoApi = KontaktinformasjonApi(url, httpClient)

    override fun hentGeografiskTilknyttning(enhet: EnhetId): List<EnhetGeografiskTilknyttning> {
        return enhetApi
            .getNavKontorerByEnhetsnummerUsingGET(enhet.get())
            .map(::toInternalDomain)
    }

    override fun hentEnheter(
        enhetId: String?,
        oppgaveBehandlende: OppgaveBehandlerFilter,
        enhetStatuser: List<EnhetStatus>
    ): List<Enhet> {
        return enhetApi
            .getAllEnheterUsingGET(
                enhetStatusListe = enhetStatuser.map { it.name },
                enhetsnummerListe = enhetId?.let { listOf(it) } ?: emptyList(),
                oppgavebehandlerFilter = oppgaveBehandlende.name
            )
            .map(::toInternalDomain)
    }

    override fun finnNavKontor(geografiskTilknytning: String, diskresjonskode: NorgDomain.DiskresjonsKode?): Enhet {
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
    ): List<Enhet> {
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

    override fun hentKontaktinfo(enhet: String): EnhetKontaktinformasjon {
        return enhetKontaktinfoApi
            .getKontaktinformasjonUsingGET(enhet)
            .let(::toInternalDomain)
    }

    private fun toInternalDomain(kontor: RsNavKontorDTO) = EnhetGeografiskTilknyttning(
        alternativEnhetId = kontor.alternativEnhetId?.toString(),
        enhetId = kontor.enhetId?.toString(),
        geografiskOmraade = kontor.geografiskOmraade,
        navKontorId = kontor.navKontorId?.toString()
    )

    private fun toInternalDomain(enhet: RsEnhetDTO) = Enhet(
        enhetId = enhet.enhetNr,
        enhetNavn = enhet.navn,
        status = enhet.status?.let(EnhetStatus::valueOf)
    )

    private fun toInternalDomain(enhet: RsEnhetKontaktinformasjonDTO) = EnhetKontaktinformasjon(
        enhetId = requireNotNull(enhet.enhetNr),
        enhetNavn = requireNotNull(""), // TODO etterspørr om dete kan legges til i APIet
        publikumsmottak = enhet.publikumsmottak?.map { toInternalDomain(it) } ?: emptyList()
    )

    private fun toInternalDomain(mottak: RsPublikumsmottakDTO) = NorgDomain.Publikumsmottak(
        besoksadresse = mottak.besoeksadresse?.let { toInternalDomain(it) },
        apningstider = mottak.aapningstider?.map { toInternalDomain(it) } ?: emptyList()
    )

    private fun toInternalDomain(adresse: RsStedsadresseDTO) = NorgDomain.Gateadresse(
        gatenavn = adresse.gatenavn,
        husnummer = adresse.husnummer,
        husbokstav = adresse.husbokstav,
        postnummer = adresse.postnummer,
        poststed = adresse.poststed
    )

    private fun toInternalDomain(aapningstid: RsAapningstidDTO) = NorgDomain.Apningstid(
        ukedag = requireNotNull(aapningstid.dag).uppercase().let(NorgDomain.Ukedag::valueOf),
        apentFra = requireNotNull(aapningstid.fra),
        apentTil = requireNotNull(aapningstid.til)
    )
}
