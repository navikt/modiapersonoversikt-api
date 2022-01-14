package no.nav.modiapersonoversikt.infrastructure.cache.redis

import com.fasterxml.jackson.core.type.TypeReference
import no.nav.modiapersonoversikt.config.JacksonConfig
import org.springframework.cache.Cache
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.AbstractValueAdaptingCache
import redis.clients.jedis.HostAndPort
import redis.clients.jedis.Jedis
import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.locks.ReentrantLock

@Suppress("UNCHECKED_CAST")
class RedisCaffeineCache<T>(
    val config: Config<T>
) : AbstractValueAdaptingCache(config.allowNullValues) {
    data class Config<T>(
        val name: String,
        val hostAndPort: HostAndPort,
        val expiry: Duration?,
        val localCache: CaffeineCache,
        val type: TypeReference<T>,
        val allowNullValues: Boolean
    )
    data class CacheMessage(val cacheName: String, val value: Any?)
    private val lock = ReentrantLock()
    private val jedis = Jedis(config.hostAndPort)
    private val mapper = JacksonConfig.mapper

    override fun getName(): String = config.name

    override fun getNativeCache(): RedisCaffeineCache<T> = this

    override fun <T : Any?> get(key: Any, valueLoader: Callable<T>): T? {
        var value: Any? = lookup(key)
        if (value != null) {
            return value as T
        }
        try {
            lock.lock()
            value = lookup(key)
            if (value != null) {
                return value as T
            }
            value = valueLoader.call()
            put(key, toStoreValue(value))
            return value
        } catch (e: Exception) {
            throw Cache.ValueRetrievalException(key, valueLoader, e)
        } finally {
            lock.unlock()
        }
    }

    override fun put(key: Any, value: Any?) {
        if (!config.allowNullValues && value == null) {
            evict(key)
        } else {
            val expire = config.expiry?.seconds ?: -1
            if (expire > 0) {
                jedis.setex(serializeKey(key), expire, serialize(value))
            } else {
                jedis.set(serializeKey(key), serialize(value))
            }

            push(CacheMessage(config.name, key))
            config.localCache.put(key, value)
        }
    }

    override fun putIfAbsent(key: Any, value: Any?): Cache.ValueWrapper? {
        return super.putIfAbsent(key, value)
    }

    override fun evict(key: Any) {
        jedis.del(serializeKey(key))
        config.localCache.evict(key)
    }

    override fun clear() {
        val keys: Set<String> = jedis.keys("$name:*")
        jedis.del(*keys.toTypedArray())
        push(CacheMessage(config.name, null))
        config.localCache.clear()
    }

    override fun lookup(key: Any): Any? {
        var value: Any? = config.localCache.get(key)
        if (value != null) {
            return value
        }
        value = deserialize(jedis.get(serializeKey(key)))
        if (value != null) {
            config.localCache.put(key, value)
        }
        return value
    }

    private fun push(message: CacheMessage) {
        jedis.publish("cache-change", serialize(message))
    }

    private fun serialize(value: Any?): String {
        return mapper.writeValueAsString(value)
    }

    private fun serializeKey(value: Any?): String = "$name:${serialize(value)}"

    private fun deserialize(value: String?): T? {
        if (value == null) return null
        return mapper.readValue(value, config.type)
    }
}
