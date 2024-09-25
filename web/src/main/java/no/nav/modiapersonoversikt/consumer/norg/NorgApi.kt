package no.nav.modiapersonoversikt.consumer.norg

import kotlinx.coroutines.runBlocking
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
import no.nav.modiapersonoversikt.consumer.norg.generated.apis.ArbeidsfordelingApi
import no.nav.modiapersonoversikt.consumer.norg.generated.apis.EnhetApi
import no.nav.modiapersonoversikt.consumer.norg.generated.apis.EnhetskontaktinfoApi
import no.nav.modiapersonoversikt.consumer.norg.generated.apis.OrganiseringApi
import no.nav.modiapersonoversikt.consumer.norg.generated.models.*
import no.nav.modiapersonoversikt.infrastructure.cache.CacheUtils
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import no.nav.modiapersonoversikt.utils.isNumeric
import no.nav.personoversikt.common.utils.Retry
import okhttp3.OkHttpClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

interface NorgApi : Pingable {
    companion object {
        @JvmStatic
        val IKKE_NEDLAGT: List<EnhetStatus> = EnhetStatus.entries.minus(EnhetStatus.NEDLAGT)
    }

    fun hentGeografiskTilknyttning(enhet: EnhetId): List<EnhetGeografiskTilknyttning>

    fun hentEnheter(
        enhetId: EnhetId?,
        oppgaveBehandlende: OppgaveBehandlerFilter = OppgaveBehandlerFilter.UFILTRERT,
        enhetStatuser: List<EnhetStatus> = IKKE_NEDLAGT,
    ): List<Enhet>

    fun finnNavKontor(
        geografiskTilknytning: String,
        diskresjonskode: NorgDomain.DiskresjonsKode?,
    ): Enhet?

    fun hentRegionalEnheter(enhet: List<EnhetId>): List<EnhetId>

    fun hentRegionalEnhet(enhet: EnhetId): EnhetId?

    fun hentBehandlendeEnheter(
        behandling: Behandling?,
        geografiskTilknyttning: String?,
        oppgavetype: String?,
        fagomrade: String?,
        erEgenAnsatt: Boolean?,
        diskresjonskode: NorgDomain.DiskresjonsKode?,
    ): List<Enhet>

    fun hentKontaktinfo(enhet: EnhetId): EnhetKontaktinformasjon
}

class NorgApiImpl(
    private val url: String,
    httpClient: OkHttpClient,
    scheduler: Timer = Timer(),
    private val clock: Clock = Clock.systemDefaultZone(),
) : NorgApi {
    private val log: Logger = LoggerFactory.getLogger(NorgApi::class.java)
    private val cacheRetention = Duration.ofHours(1)
    private val cacheGraceperiod = Duration.ofMinutes(2)
    private var cache: Map<EnhetId, EnhetKontaktinformasjon> = emptyMap()
    private var lastUpdateOfCache: LocalDateTime? = null
    private val navkontorCache = createNorgCache<String, Enhet>()
    private val gtCache = createNorgCache<String, List<EnhetGeografiskTilknyttning>>()
    private val regionalkontorCache = createNorgCache<EnhetId, EnhetId>()

    private val retry =
        Retry(
            Retry.Config(
                initDelay = 30.seconds,
                growthFactor = 2.0,
                delayLimit = 1.hours,
                scheduler = scheduler,
            ),
        )

    private val arbeidsfordelingApi = ArbeidsfordelingApi(url, httpClient)
    private val enhetApi = EnhetApi(url, httpClient)
    private val enhetKontaktInfoApi = EnhetskontaktinfoApi(url, httpClient)
    private val organiseringApi = OrganiseringApi(url, httpClient)

    init {
        hentEnheterOgKontaktinformasjon()
        scheduler.scheduleAtFixedRate(
            delay = cacheRetention.toMillis(),
            period = cacheRetention.toMillis(),
            action = {
                hentEnheterOgKontaktinformasjon()
            },
        )
    }

    override fun hentGeografiskTilknyttning(enhet: EnhetId): List<EnhetGeografiskTilknyttning> =
        gtCache.get(enhet.get()) {
            enhetApi
                .getNavKontorerByEnhetsnummerUsingGET(it)
                ?.map(::toInternalDomain)
        } ?: emptyList()

    override fun hentEnheter(
        enhetId: EnhetId?,
        oppgaveBehandlende: OppgaveBehandlerFilter,
        enhetStatuser: List<EnhetStatus>,
    ): List<Enhet> {
        val kandidater = if (enhetId != null) listOfNotNull(cache[enhetId]) else cache.values
        val oppgaveBehandlerFilter: (EnhetKontaktinformasjon) -> Boolean =
            when (oppgaveBehandlende) {
                OppgaveBehandlerFilter.UFILTRERT -> ({ true })
                OppgaveBehandlerFilter.INGEN_OPPGAVEBEHANDLERE -> ({ !it.enhet.oppgavebehandler })
                OppgaveBehandlerFilter.KUN_OPPGAVEBEHANDLERE -> ({ it.enhet.oppgavebehandler })
            }

        return kandidater
            .filter(oppgaveBehandlerFilter)
            .filter { enhetStatuser.contains(it.enhet.status) }
            .map { it.enhet }
    }

    override fun finnNavKontor(
        geografiskTilknytning: String,
        diskresjonskode: NorgDomain.DiskresjonsKode?,
    ): Enhet? {
        val key = "finnNavKontor[$geografiskTilknytning,$diskresjonskode]"
        return navkontorCache.get(key) {
            if (geografiskTilknytning.isNumeric()) {
                enhetApi
                    .getEnhetByGeografiskOmraadeUsingGET(
                        geografiskOmraade = geografiskTilknytning,
                        disk = diskresjonskode?.name,
                    )?.let(::toInternalDomain)
            } else {
                /**
                 * Ikke numerisk GT tilsier at det er landkode pga utenlandsk GT og da har vi ingen enhet
                 */
                null
            }
        }
    }

    override fun hentRegionalEnheter(enhet: List<EnhetId>): List<EnhetId> = enhet.mapNotNull(::hentRegionalEnhet)

    override fun hentRegionalEnhet(enhet: EnhetId): EnhetId? =
        regionalkontorCache.get(enhet) { enhetId ->
            organiseringApi
                .getAllOrganiseringerForEnhetUsingGET(enhetId.get())
                ?.firstOrNull { it.orgType == "FYLKE" }
                ?.organiserer
                ?.nr
                ?.let(::EnhetId)
        }

    override fun hentBehandlendeEnheter(
        behandling: Behandling?,
        geografiskTilknyttning: String?,
        oppgavetype: String?,
        fagomrade: String?,
        erEgenAnsatt: Boolean?,
        diskresjonskode: NorgDomain.DiskresjonsKode?,
    ): List<Enhet> =
        arbeidsfordelingApi
            .runCatching {
                getBehandlendeEnheterUsingPOST(
                    RsArbeidsFordelingCriteriaSkjermetDTO(
                        oppgavetype = oppgavetype,
                        tema = fagomrade,
                        skjermet = erEgenAnsatt,
                        behandlingstema = behandling?.behandlingstema,
                        behandlingstype = behandling?.behandlingstype,
                        geografiskOmraade = geografiskTilknyttning ?: "",
                        diskresjonskode = diskresjonskode?.name,
                        enhetNummer = null,
                        temagruppe = null,
                    ),
                )
            }.getOrElse {
                log.error("Kunne ikke hente enheter fra arbeidsfordeling.", it)
                emptyList()
            }?.map(::toInternalDomain)
            .orEmpty()

    override fun hentKontaktinfo(enhet: EnhetId): EnhetKontaktinformasjon =
        requireNotNull(cache[enhet]) {
            "Fant ikke $enhet i cache"
        }

    override fun ping() =
        SelfTestCheck(
            """
            NorgApi via $url (${cache.size}, ${gtCache.estimatedSize()}, ${navkontorCache.estimatedSize()}, ${regionalkontorCache.estimatedSize()})
            """,
            false,
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
                    """.trimIndent(),
                )
            }
        }

    private fun hentEnheterOgKontaktinformasjon() {
        runBlocking {
            retry.run {
                cache =
                    enhetKontaktInfoApi
                        .hentAlleEnheterInkludertKontaktinformasjonUsingGET(
                            consumerId = AppConstants.APP_NAME,
                        )?.mapNotNull {
                            try {
                                toInternalDomain(it)
                            } catch (e: Exception) {
                                log.error("Kunne ikke mappe enhet til lokalt format. $it", e)
                                null
                            }
                        }?.associateBy { EnhetId(it.enhet.enhetId) }
                        .orEmpty()
                lastUpdateOfCache = LocalDateTime.now(clock)
            }
        }
    }

    private fun <KEY, VALUE> createNorgCache() =
        CacheUtils.createCache<KEY, VALUE>(
            expireAfterWrite = cacheRetention,
            maximumSize = 2000,
        )

    companion object {
        internal fun toInternalDomain(kontor: RsNavKontorDTO) =
            EnhetGeografiskTilknyttning(
                alternativEnhetId = kontor.alternativEnhetId?.toString(),
                enhetId = kontor.enhetId?.toString(),
                geografiskOmraade = kontor.geografiskOmraade,
                navKontorId = kontor.navKontorId?.toString(),
            )

        internal fun toInternalDomain(enhet: RsEnhetDTO) =
            Enhet(
                enhetId = requireNotNull(enhet.enhetNr),
                enhetNavn = requireNotNull(enhet.navn),
                status = EnhetStatus.safeValueOf(enhet.status),
                oppgavebehandler = requireNotNull(enhet.oppgavebehandler),
            )

        internal fun toInternalDomain(enhet: RsEnhetInkludertKontaktinformasjonDTO) =
            EnhetKontaktinformasjon(
                enhet = toInternalDomain(requireNotNull(enhet.enhet)),
                publikumsmottak =
                    enhet.kontaktinformasjon?.publikumsmottak?.map { toInternalDomain(it) }
                        ?: emptyList(),
                overordnetEnhet = enhet.overordnetEnhet?.let(::EnhetId),
            )

        private fun toInternalDomain(mottak: RsPublikumsmottakDTO) =
            NorgDomain.Publikumsmottak(
                besoksadresse = mottak.besoeksadresse?.let { toInternalDomain(it) },
                apningstider =
                    mottak.aapningstider
                        ?.filter { it.dag != null }
                        ?.map { toInternalDomain(it) } ?: emptyList(),
            )

        private fun toInternalDomain(adresse: RsStedsadresseDTO) =
            NorgDomain.Gateadresse(
                gatenavn = adresse.gatenavn,
                husnummer = adresse.husnummer,
                husbokstav = adresse.husbokstav,
                postnummer = adresse.postnummer,
                poststed = adresse.poststed,
            )

        private fun toInternalDomain(aapningstid: RsAapningstidDTO) =
            NorgDomain.Apningstid(
                ukedag = NorgDomain.Ukedag.safeValueOf(aapningstid.dag),
                stengt = aapningstid.stengt ?: false,
                apentFra = aapningstid.fra,
                apentTil = aapningstid.til,
            )
    }
}
