package no.nav.modiapersonoversikt.consumer.norg

import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.commondomain.Behandling
import no.nav.modiapersonoversikt.config.AppConstants
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain.Enhet
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain.EnhetGeografiskTilknyttning
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain.EnhetKontaktinformasjon
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain.EnhetStatus
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain.OppgaveBehandlerFilter
import no.nav.modiapersonoversikt.infrastructure.types.Pingable
import no.nav.modiapersonoversikt.legacy.api.domain.norg.generated.apis.ArbeidsfordelingApi
import no.nav.modiapersonoversikt.legacy.api.domain.norg.generated.apis.EnhetApi
import no.nav.modiapersonoversikt.legacy.api.domain.norg.generated.apis.EnhetskontaktinfoApi
import no.nav.modiapersonoversikt.legacy.api.domain.norg.generated.models.*
import no.nav.modiapersonoversikt.utils.Retry
import no.nav.modiapersonoversikt.utils.isNumeric
import okhttp3.OkHttpClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

interface NorgApi : Pingable {
    companion object {
        @JvmStatic
        val IKKE_NEDLAGT: List<EnhetStatus> = EnhetStatus.values().asList().minus(EnhetStatus.NEDLAGT)
    }

    fun hentGeografiskTilknyttning(enhet: EnhetId): List<EnhetGeografiskTilknyttning>

    fun hentEnheter(
        enhetId: EnhetId?,
        oppgaveBehandlende: OppgaveBehandlerFilter = OppgaveBehandlerFilter.UFILTRERT,
        enhetStatuser: List<EnhetStatus> = IKKE_NEDLAGT
    ): List<Enhet>

    fun finnNavKontor(geografiskTilknytning: String, diskresjonskode: NorgDomain.DiskresjonsKode?): Enhet?

    fun hentBehandlendeEnheter(
        behandling: Behandling?,
        geografiskTilknyttning: String?,
        oppgavetype: String?,
        fagomrade: String?,
        erEgenAnsatt: Boolean?,
        diskresjonskode: String?
    ): List<Enhet>

    fun hentKontaktinfo(enhet: EnhetId): EnhetKontaktinformasjon
}

class NorgApiImpl(
    private val url: String,
    httpClient: OkHttpClient,
    scheduler: Timer = Timer(),
    private val clock: Clock = Clock.systemDefaultZone()
) : NorgApi {
    private val log: Logger = LoggerFactory.getLogger(NorgApi::class.java)
    private val cacheRetention = Duration.ofHours(1)
    private val cacheGraceperiod = Duration.ofMinutes(2)
    private var cache: Map<EnhetId, EnhetKontaktinformasjon> = emptyMap()
    private var lastUpdateOfCache: LocalDateTime? = null
    private val navkontorCache = createNorgCache<String, Enhet>()
    private val gtCache = createNorgCache<String, List<EnhetGeografiskTilknyttning>>()

    private val retry = Retry(
        Retry.Config(
            initDelay = 30 * 1000,
            growthFactor = 2.0,
            delayLimit = 60 * 60 * 1000,
            scheduler = scheduler
        )
    )

    private val arbeidsfordelingApi = ArbeidsfordelingApi(url, httpClient)
    private val enhetApi = EnhetApi(url, httpClient)
    private val enhetKontaktInfoApi = EnhetskontaktinfoApi(url, httpClient)

    init {
        hentEnheterOgKontaktinformasjon()
        scheduler.scheduleAtFixedRate(
            delay = cacheRetention.toMillis(),
            period = cacheRetention.toMillis(),
            action = { hentEnheterOgKontaktinformasjon() }
        )
    }

    override fun hentGeografiskTilknyttning(enhet: EnhetId): List<EnhetGeografiskTilknyttning> {
        return gtCache.get(enhet.get()) {
            enhetApi
                .getNavKontorerByEnhetsnummerUsingGET(it)
                .map(::toInternalDomain)
        } ?: emptyList()
    }

    override fun hentEnheter(
        enhetId: EnhetId?,
        oppgaveBehandlende: OppgaveBehandlerFilter,
        enhetStatuser: List<EnhetStatus>
    ): List<Enhet> {
        val kandidater = if (enhetId != null) listOfNotNull(cache[enhetId]) else cache.values
        val oppgaveBehandlerFilter: (EnhetKontaktinformasjon) -> Boolean = when (oppgaveBehandlende) {
            OppgaveBehandlerFilter.UFILTRERT -> ({ true })
            OppgaveBehandlerFilter.INGEN_OPPGAVEBEHANDLERE -> ({ !it.enhet.oppgavebehandler })
            OppgaveBehandlerFilter.KUN_OPPGAVEBEHANDLERE -> ({ it.enhet.oppgavebehandler })
        }

        return kandidater
            .filter(oppgaveBehandlerFilter)
            .filter { enhetStatuser.contains(it.enhet.status) }
            .map { it.enhet }
    }

    override fun finnNavKontor(geografiskTilknytning: String, diskresjonskode: NorgDomain.DiskresjonsKode?): Enhet? {
        val key = "finnNavKontor[$geografiskTilknytning,$diskresjonskode]"
        return navkontorCache.get(key) {
            if (geografiskTilknytning.isNumeric()) {
                enhetApi.getEnhetByGeografiskOmraadeUsingGET(
                    geografiskOmraade = geografiskTilknytning,
                    disk = diskresjonskode?.name
                ).let(::toInternalDomain)
            } else {
                /**
                 * Ikke numerisk GT tilsier at det er landkode pga utenlandsk GT og da har vi ingen enhet
                 */
                null
            }
        }
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

    override fun hentKontaktinfo(enhet: EnhetId): EnhetKontaktinformasjon {
        return requireNotNull(cache[enhet]) {
            "Fant ikke $enhet i cache"
        }
    }

    override fun ping() = SelfTestCheck(
        "NorgApi via $url (${cache.size}, ${gtCache.estimatedSize()}, ${navkontorCache.estimatedSize()})",
        false
    ) {
        val limit = LocalDateTime.now(clock).minus(cacheRetention).plus(cacheGraceperiod)
        val cacheIsFresh = lastUpdateOfCache?.isAfter(limit) == true

        if (cacheIsFresh && cache.isNotEmpty()) {
            HealthCheckResult.healthy()
        } else {
            HealthCheckResult.unhealthy(
                """
                Last updated: $lastUpdateOfCache
                CacheSize: ${cache.size}
                """.trimIndent()
            )
        }
    }

    private fun hentEnheterOgKontaktinformasjon() {
        retry.run {
            cache = enhetKontaktInfoApi
                .hentAlleEnheterInkludertKontaktinformasjonUsingGET(
                    consumerId = AppConstants.SYSTEMUSER_USERNAME
                )
                .mapNotNull {
                    try {
                        toInternalDomain(it)
                    } catch (e: Exception) {
                        log.error("Kunne ikke mappe enhet til lokalt format. $it", e)
                        null
                    }
                }
                .associateBy { EnhetId(it.enhet.enhetId) }
            lastUpdateOfCache = LocalDateTime.now(clock)
        }
    }

    private fun <KEY, VALUE> createNorgCache() = Caffeine
        .newBuilder()
        .maximumSize(2000)
        .expireAfterWrite(cacheRetention)
        .build<KEY, VALUE>()

    companion object {
        internal fun toInternalDomain(kontor: RsNavKontorDTO) = EnhetGeografiskTilknyttning(
            alternativEnhetId = kontor.alternativEnhetId?.toString(),
            enhetId = kontor.enhetId?.toString(),
            geografiskOmraade = kontor.geografiskOmraade,
            navKontorId = kontor.navKontorId?.toString()
        )

        internal fun toInternalDomain(enhet: RsEnhetDTO) = Enhet(
            enhetId = requireNotNull(enhet.enhetNr),
            enhetNavn = requireNotNull(enhet.navn),
            status = EnhetStatus.safeValueOf(enhet.status),
            oppgavebehandler = requireNotNull(enhet.oppgavebehandler)
        )

        internal fun toInternalDomain(enhet: RsEnhetInkludertKontaktinformasjonDTO) = EnhetKontaktinformasjon(
            enhet = toInternalDomain(requireNotNull(enhet.enhet)),
            publikumsmottak = enhet.kontaktinformasjon?.publikumsmottak?.map { toInternalDomain(it) } ?: emptyList()
        )

        private fun toInternalDomain(mottak: RsPublikumsmottakDTO) = NorgDomain.Publikumsmottak(
            besoksadresse = mottak.besoeksadresse?.let { toInternalDomain(it) },
            apningstider = mottak.aapningstider
                ?.filter { it.dag != null }
                ?.map { toInternalDomain(it) } ?: emptyList()
        )

        private fun toInternalDomain(adresse: RsStedsadresseDTO) = NorgDomain.Gateadresse(
            gatenavn = adresse.gatenavn,
            husnummer = adresse.husnummer,
            husbokstav = adresse.husbokstav,
            postnummer = adresse.postnummer,
            poststed = adresse.poststed
        )

        private fun toInternalDomain(aapningstid: RsAapningstidDTO) = NorgDomain.Apningstid(
            ukedag = NorgDomain.Ukedag.safeValueOf(aapningstid.dag),
            stengt = aapningstid.stengt ?: false,
            apentFra = aapningstid.fra,
            apentTil = aapningstid.til
        )
    }
}
