package no.nav.modiapersonoversikt.consumer.nom

import com.github.benmanes.caffeine.cache.Cache
import no.nav.common.client.nom.NomClient
import no.nav.common.client.nom.VeilederNavn
import no.nav.common.health.HealthCheckResult
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.infrastructure.cache.CacheConfig
import java.util.*
import kotlin.time.Duration.Companion.hours

class NullCachingNomClient(
    private val nomClient: NomClient,
    private val cache: Cache<NavIdent, Optional<VeilederNavn>> = CacheConfig.createCache(12.hours.inWholeSeconds, 10_000L).build(),
) : NomClient {
    override fun finnNavn(navIdent: NavIdent): VeilederNavn {
        val cachedValue =
            checkNotNull(
                cache.get(navIdent) {
                    Optional.ofNullable(nomClient.finnNavn(navIdent))
                },
            )
        return cachedValue
            .orElseThrow { IllegalStateException("Fant ikke navn for NAV-ident: $navIdent") }
    }

    override fun finnNavn(navIdenter: List<NavIdent>): List<VeilederNavn> {
        val out = mutableListOf<VeilederNavn>()
        val uncachedIdenter = mutableListOf<NavIdent>()

        navIdenter.forEach { ident ->
            val cachedValue = cache.getIfPresent(ident)
            if (cachedValue == null) {
                uncachedIdenter.add(ident)
            } else if (cachedValue.isPresent) {
                out.add(cachedValue.get())
            }
        }

        if (uncachedIdenter.isNotEmpty()) {
            uncachedIdenter.forEach { cache.put(it, Optional.empty()) }

            val nyeVeileder = nomClient.finnNavn(uncachedIdenter)
            nyeVeileder
                .filterNotNull()
                .forEach { cache.put(it.navIdent, Optional.of(it)) }
            out.addAll(nyeVeileder)
        }

        return out
    }

    override fun checkHealth(): HealthCheckResult = nomClient.checkHealth()
}
