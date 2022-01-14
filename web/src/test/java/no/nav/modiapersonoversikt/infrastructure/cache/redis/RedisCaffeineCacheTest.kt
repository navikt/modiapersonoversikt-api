package no.nav.modiapersonoversikt.infrastructure.cache.redis

import com.fasterxml.jackson.core.type.TypeReference
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.cache.caffeine.CaffeineCache
import java.time.Duration

data class CacheData(val id: Long, val name: String)
data class CacheKey(val id: Long)

internal class RedisCaffeineCacheTest : TestUtils.WithRedis {
    val testCache = createRedisCache("test-cache", Duration.ofSeconds(2))
    val otherCache = createRedisCache("other-cache", Duration.ofSeconds(2))

    @Test
    internal fun `skal lagre cache verdi i begge cachene`() = runBlocking {
        val (cache, caffeine) = testCache
        val (otherCache, otherCaffeine) = otherCache
        val key = CacheKey(1)

        cache.get(key) { CacheData(1, "data") }
        cache.get(key) { CacheData(1, "data") }
        otherCache.get(key) { CacheData(1, "data") }
        otherCache.get(key) { CacheData(1, "data") }

        assertThat(caffeine.estimatedSize()).isEqualTo(1)
        assertThat(otherCaffeine.estimatedSize()).isEqualTo(1)
        assertThat(getRedisKeys("*")).hasSize(2)
        assertThat(getRedisKeys("test-cache:*")).hasSize(1)
        assertThat(getRedisKeys("other-cache:*")).hasSize(1)

        cache.clear()

        assertThat(caffeine.estimatedSize()).isEqualTo(0)
        assertThat(otherCaffeine.estimatedSize()).isEqualTo(1)
        assertThat(getRedisKeys("*")).hasSize(1)
        assertThat(getRedisKeys("test-cache:*")).hasSize(0)
        assertThat(getRedisKeys("other-cache:*")).hasSize(1)

        delay(2500)
        caffeine.cleanUp()
        otherCaffeine.cleanUp()

        assertThat(caffeine.estimatedSize()).isEqualTo(0)
        assertThat(otherCaffeine.estimatedSize()).isEqualTo(0)
        assertThat(getRedisKeys("*")).hasSize(0)
        assertThat(getRedisKeys("test-cache:*")).hasSize(0)
        assertThat(getRedisKeys("other-cache:*")).hasSize(0)

        Unit
    }

    fun createRedisCache(cacheName: String, expiry: Duration): Pair<RedisCaffeineCache<CacheData>, Cache<Any?, Any?>> {
        val caffeine: Cache<Any?, Any?> = Caffeine
            .newBuilder()
            .expireAfterAccess(expiry)
            .expireAfterWrite(expiry)
            .build()
        val cache = RedisCaffeineCache(
            RedisCaffeineCache.Config(
                name = cacheName,
                hostAndPort = redisHostAndPort(),
                expiry = expiry,
                localCache = CaffeineCache(cacheName, caffeine, true),
                type = object : TypeReference<CacheData>() {},
                allowNullValues = true
            )
        )

        return cache to caffeine
    }
}
