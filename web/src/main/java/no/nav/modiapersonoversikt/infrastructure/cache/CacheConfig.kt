package no.nav.modiapersonoversikt.infrastructure.cache

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.ImportResource
import java.time.Duration
import java.util.*

@Configuration
@EnableCaching
@EnableAspectJAutoProxy
@ImportResource("classpath*:*cacheconfig.xml")
open class CacheConfig {
    @Bean
    open fun cacheManager(): CacheManager {
        return CaffeineCacheManager().apply {
            cache("abacClientCache", 3600, 10000)
            cache("endpointCache", 3, 10000)
            cache("kjerneinformasjonCache", 60)
            cache("ytelseskontrakter", 600)
            cache("pleiePengerCache", 300)
            cache("oppfolgingsinfoCache", 300)
            cache("oppfolgingCache", 600)
            cache("foreldrePengerCache", 300)
            cache("hentSykmeldingsperioderCache", 300)
            cache("utbetalingCache", 1800, 10000)
            cache("pdlCache", 3600, 100000)
            cache("asbogosysEnhet", 86400, 10000)
            cache("ldap", 3600, 20000)
            cache("varslingCache", 180, 10000)
            cache("innsynJournalCache", 1800, 10000)
            cache("pesysCache", 600)
        }
    }

    private fun CaffeineCacheManager.cache(name: String, time: Int, maximumSize: Int = 1000) {
        val cache = Caffeine.newBuilder()
            .recordStats()
            .expireAfterAccess(Duration.ofSeconds(time.toLong()))
            .expireAfterWrite(Duration.ofSeconds(time.toLong()))
            .maximumSize(maximumSize.toLong())
            .build<Any, Any>()

        this.registerCustomCache(name, cache)
    }
}
