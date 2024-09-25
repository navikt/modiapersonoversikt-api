package no.nav.modiapersonoversiktproxy.infrastructure.cache

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import java.time.Duration

@Configuration
@EnableCaching
@EnableAspectJAutoProxy
open class CacheConfig {
    @Bean
    open fun cacheManager(): CacheManager =
        CaffeineCacheManager().apply {
            cache("endpointCache", 3, 10000)
            cache("ytelseskontrakterCache", 600)
            cache("pleiePengerCache", 300)
            cache("oppfolgingskontraktCache", 600)
            cache("foreldrePengerCache", 300)
            cache("sykePengerCache", 300)
        }

    @Bean("userkeygenerator")
    open fun userKeyGenerator() = AutentisertBrukerKeyGenerator()

    @Bean("methodawarekeygenerator")
    open fun methodAwareKeyGenerator() = MethodAwareKeyGenerator()

    private fun CaffeineCacheManager.cache(
        name: String,
        time: Long,
        maximumSize: Long = 1000,
    ) {
        this.registerCustomCache(name, createCache(time, maximumSize).build())
    }

    companion object {
        fun createCache(
            time: Long,
            maximumSize: Long = 1000,
        ) = Caffeine
            .newBuilder()
            .recordStats()
            .expireAfterAccess(Duration.ofSeconds(time))
            .expireAfterWrite(Duration.ofSeconds(time))
            .maximumSize(maximumSize)
    }
}
