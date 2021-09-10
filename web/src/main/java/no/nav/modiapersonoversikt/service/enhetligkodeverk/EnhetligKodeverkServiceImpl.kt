package no.nav.modiapersonoversikt.service.enhetligkodeverk

import java.time.*
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class EnhetligKodeverkServiceImpl(val providers: KodeverkProviders) : EnhetligKodeverk.Service {

    private val cache: MutableMap<KodeverkConfig, EnhetligKodeverk.Kodeverk> = mutableMapOf()
    private val scheduler = Timer()

    init {
        val schedulertDatoTidspunkt = hentScheduleDatoInstant()

        if (schedulertDatoTidspunkt.isAfter(Instant.now())) {
            prepopulerCache()
        }

        scheduler.scheduleAtFixedRate(
            time = Date.from(schedulertDatoTidspunkt),
            period = Duration.ofHours(24).toMillis()
        ) {
            prepopulerCache()
        }
    }

    private fun hentScheduleDatoInstant(): Instant {
        // Kl 01:00 den aktuelle dagen
        return LocalDate.now().atTime(1, 0)
            .atZone(ZoneId.systemDefault())
            .toInstant()
    }

    private fun prepopulerCache() {
        KodeverkConfig.values().forEach { config ->
            cache[config] = config.hentKodeverk(providers)
        }
        println("Prepoluerte cache")
    }

    override fun hentKodeverk(kodeverkNavn: KodeverkConfig): EnhetligKodeverk.Kodeverk {
        return requireNotNull(cache[kodeverkNavn]) {
            "Fant ikke kodeverkNavn $kodeverkNavn"
        }
    }
}
