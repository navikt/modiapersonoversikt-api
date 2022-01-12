package no.nav.modiapersonoversikt.service.enhetligkodeverk

import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.modiapersonoversikt.utils.Retry
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class EnhetligKodeverkServiceImpl(
    private val providers: KodeverkProviders,
    scheduler: Timer = Timer(),
    private val clock: Clock = Clock.systemDefaultZone()
) : EnhetligKodeverk.Service {
    private data class KodeverkCacheEntry(val timestamp: LocalDateTime, val kodeverk: EnhetligKodeverk.Kodeverk)

    private val emptyKodeverk = EnhetligKodeverk.Kodeverk("EMPTY", emptyMap())
    private val cache: MutableMap<KodeverkConfig, KodeverkCacheEntry> = mutableMapOf()
    private val cacheRetention = Duration.ofHours(24)
    private val cacheGraceperiod = Duration.ofMinutes(15)
    private val retry = Retry(
        Retry.Config(
            initDelay = 30 * 1000,
            growthFactor = 2.0,
            delayLimit = 60 * 60 * 1000,
            scheduler = scheduler
        )
    )

    init {
        prepopulerCache()

        scheduler.scheduleAtFixedRate(
            time = Date.from(hentScheduleDatoInstant()),
            period = cacheRetention.toMillis()
        ) {
            prepopulerCache()
        }
    }

    private fun hentScheduleDatoInstant(): Instant {
        val kjoringIdag = LocalDate.now(clock).atTime(1, 0)
            .atZone(ZoneId.systemDefault())
            .toInstant()

        return if (kjoringIdag.isBefore(Instant.now())) {
            // Flytter en dag frem i tid, siden tidspunktet allerede er passert
            kjoringIdag.plus(1, ChronoUnit.DAYS)
        } else {
            kjoringIdag
        }
    }

    override fun hentKodeverk(kodeverkNavn: KodeverkConfig): EnhetligKodeverk.Kodeverk {
        return cache[kodeverkNavn]?.kodeverk ?: emptyKodeverk
    }

    override fun ping() = SelfTestCheck(
        "EnhetligKodeverk",
        false
    ) {
        val limit = LocalDateTime.now(clock).minus(cacheRetention.plus(cacheGraceperiod))
        val missingValues: List<KodeverkConfig> = KodeverkConfig.values().toList().minus(cache.keys)
        val outdatedCacheEntries = cache.entries.filter { it.value.timestamp.isBefore(limit) }

        if (outdatedCacheEntries.isEmpty() && missingValues.isEmpty()) {
            HealthCheckResult.healthy()
        } else {
            val outdatedCaches = outdatedCacheEntries.joinToString(", ") { it.key.name }
            val missingCaches = missingValues.joinToString(", ")
            HealthCheckResult.unhealthy(
                """
                Manglende cache verdier: [$missingCaches]                
                Utdaterte cache verdier: [$outdatedCaches]
                """.trimIndent()
            )
        }
    }

    internal fun prepopulerCache() {
        KodeverkConfig.values().forEach { config ->
            retry.run {
                cache[config] = KodeverkCacheEntry(LocalDateTime.now(clock), config.hentKodeverk(providers))
            }
        }
    }
}
