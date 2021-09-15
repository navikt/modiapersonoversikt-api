package no.nav.modiapersonoversikt.service.enhetligkodeverk

import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import org.slf4j.LoggerFactory
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

    private val log = LoggerFactory.getLogger(EnhetligKodeverkServiceImpl::class.java)
    private val emptyKodeverk = EnhetligKodeverk.Kodeverk("EMPTY", emptyMap())
    private val cache: MutableMap<KodeverkConfig, KodeverkCacheEntry> = mutableMapOf()
    private val cacheRetention = Duration.ofHours(24)
    private val cacheGraceperiod = Duration.ofMinutes(15)

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
        true
    ) {
        val limit = LocalDateTime.now(clock).minus(cacheRetention.plus(cacheGraceperiod))
        val outdatedCacheEntries = cache.values.filter { it.timestamp.isBefore(limit) }

        if (outdatedCacheEntries.isEmpty()) {
            HealthCheckResult.healthy()
        } else {
            val outdatedCaches = outdatedCacheEntries.joinToString { it.kodeverk.navn }
            HealthCheckResult.unhealthy("Outdated cache entries for: $outdatedCaches")
        }
    }

    internal fun prepopulerCache() {
        KodeverkConfig.values().forEach { config ->
            try {
                cache[config] = KodeverkCacheEntry(LocalDateTime.now(clock), config.hentKodeverk(providers))
            } catch (e: Exception) {
                log.error("Feil ved uthenting av kodeverk $config")
            }
        }
    }
}
