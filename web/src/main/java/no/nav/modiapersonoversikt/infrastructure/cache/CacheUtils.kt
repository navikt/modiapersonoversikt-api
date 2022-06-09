package no.nav.modiapersonoversikt.infrastructure.cache

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import java.time.Duration

object CacheUtils {
    fun <KEY, VALUE> createCache(
        expireAfterWrite: Duration = Duration.ofHours(1),
        maximumSize: Long = 10_000
    ): Cache<KEY, VALUE> = Caffeine
        .newBuilder()
        .expireAfterWrite(expireAfterWrite)
        .maximumSize(maximumSize)
        .build()

    @JvmStatic
    fun <KEY, VALUE> createDefaultCache() = createCache<KEY, VALUE>()
}
