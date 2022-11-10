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
            cache("endpointCache", 3, 10000)
            cache("ytelseskontrakter", 600)
            cache("pleiePengerCache", 300)
            cache("oppfolgingsinfoCache", 300)
            cache("oppfolgingCache", 600)
            cache("foreldrePengerCache", 300)
            cache("hentSykmeldingsperioderCache", 300)
            cache("utbetalingCache", 1800, 10000)
            cache("pdlCache", 3600, 100000)
            cache("ldap", 3600, 20000)
            cache("varslingCache", 180, 10000)
        }
    }

    private fun CaffeineCacheManager.cache(name: String, time: Long, maximumSize: Long = 1000) {
        this.registerCustomCache(name, createCache(time, maximumSize).build())
    }

    companion object {
        fun createCache(time: Long, maximumSize: Long = 1000) = Caffeine.newBuilder()
            .recordStats()
            .expireAfterAccess(Duration.ofSeconds(time))
            .expireAfterWrite(Duration.ofSeconds(time))
            .maximumSize(maximumSize)
    }
}
