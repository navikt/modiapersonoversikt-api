package no.nav.modiapersonoversikt.service.enhetligkodeverk

import org.slf4j.LoggerFactory
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class EnhetligKodeverkServiceImpl(
    val providers: KodeverkProviders,
    scheduler: Timer = Timer()
) : EnhetligKodeverk.Service {
    private val log = LoggerFactory.getLogger(EnhetligKodeverkServiceImpl::class.java)
    private val cache: MutableMap<KodeverkConfig, EnhetligKodeverk.Kodeverk> = mutableMapOf()
    private val emptyKodeverk = EnhetligKodeverk.Kodeverk("EMPTY", emptyMap())

    init {
        prepopulerCache()

        scheduler.scheduleAtFixedRate(
            time = Date.from(hentScheduleDatoInstant()),
            period = Duration.ofHours(24).toMillis()
        ) {
            prepopulerCache()
        }
    }

    private fun hentScheduleDatoInstant(): Instant {
        val kjoringIdag = LocalDate.now().atTime(1, 0)
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
        return cache[kodeverkNavn] ?: emptyKodeverk
    }

    internal fun prepopulerCache() {
        KodeverkConfig.values().forEach { config ->
            try {
                cache[config] = config.hentKodeverk(providers)
            } catch (e: Exception) {
                log.error("Feil ved uthenting av kodeverk $config")
            }
        }
    }
}
