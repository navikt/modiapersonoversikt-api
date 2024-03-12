package no.nav.modiapersonoversikt.service.enhetligkodeverk

import kotlinx.coroutines.runBlocking
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.KodeverkProviders
import no.nav.personoversikt.common.utils.Retry
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

class EnhetligKodeverkServiceImpl(
    private val providers: KodeverkProviders,
    scheduler: Timer = Timer(),
    private val clock: Clock = Clock.systemDefaultZone(),
) : EnhetligKodeverk.Service {
    private data class KodeverkCacheEntry(val timestamp: LocalDateTime, val kodeverk: EnhetligKodeverk.Kodeverk<*, *>)

    private val emptyKodeverk = EnhetligKodeverk.Kodeverk<Nothing, Nothing>("EMPTY", emptyMap())
    private val cache: MutableMap<EnhetligKodeverk.Kilde<*, *>, KodeverkCacheEntry> = mutableMapOf()
    private val cacheRetention = Duration.ofHours(24)
    private val cacheGraceperiod = Duration.ofMinutes(15)
    private val retry =
        Retry(
            Retry.Config(
                initDelay = 30.seconds,
                growthFactor = 2.0,
                delayLimit = 1.hours,
                scheduler = scheduler,
            ),
        )

    init {
        prepopulerCache()

        scheduler.scheduleAtFixedRate(
            time = Date.from(hentScheduleDatoInstant()),
            period = cacheRetention.toMillis(),
        ) {
            prepopulerCache()
        }
    }

    private fun hentScheduleDatoInstant(): Instant {
        val kjoringIdag =
            LocalDate.now(clock).atTime(1, 0)
                .atZone(ZoneId.systemDefault())
                .toInstant()

        return if (kjoringIdag.isBefore(Instant.now())) {
            // Flytter en dag frem i tid, siden tidspunktet allerede er passert
            kjoringIdag.plus(1, ChronoUnit.DAYS)
        } else {
            kjoringIdag
        }
    }

    override fun <KEY, VALUE> hentKodeverk(kilde: EnhetligKodeverk.Kilde<KEY, VALUE>): EnhetligKodeverk.Kodeverk<KEY, VALUE> {
        return (cache[kilde]?.kodeverk ?: emptyKodeverk) as EnhetligKodeverk.Kodeverk<KEY, VALUE>
    }

    override fun ping() =
        SelfTestCheck(
            "EnhetligKodeverk",
            false,
        ) {
            val limit = LocalDateTime.now(clock).minus(cacheRetention.plus(cacheGraceperiod))
            val missingValues: List<EnhetligKodeverk.Kilde<*, *>> = KodeverkConfig.values().minus(cache.keys)
            val outdatedCacheEntries = cache.entries.filter { it.value.timestamp.isBefore(limit) }

            if (outdatedCacheEntries.isEmpty() && missingValues.isEmpty()) {
                HealthCheckResult.healthy()
            } else {
                val outdatedCaches = outdatedCacheEntries.joinToString(", ") { it.key.navn }
                val missingCaches = missingValues.joinToString(", ")
                HealthCheckResult.unhealthy(
                    """
                    Manglende cache verdier: [$missingCaches]                
                    Utdaterte cache verdier: [$outdatedCaches]
                    """.trimIndent(),
                )
            }
        }

    internal fun prepopulerCache() {
        KodeverkConfig.values().forEach { config ->
            runBlocking {
                retry.run {
                    cache[config] = KodeverkCacheEntry(LocalDateTime.now(clock), config.hentKodeverk(providers))
                }
            }
        }
    }
}
