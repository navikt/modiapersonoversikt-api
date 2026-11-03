package no.nav.modiapersonoversikt.infrastructure.cache

import io.lettuce.core.RedisCommandExecutionException
import io.lettuce.core.RedisCommandTimeoutException
import io.lettuce.core.RedisConnectionException
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.cache.interceptor.CacheErrorHandler
import org.springframework.data.redis.RedisConnectionFailureException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import javax.net.ssl.SSLException

internal class ValkeyCacheErrorHandler(
    private val metrikker: MeterRegistry,
) : CacheErrorHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun handleCacheGetError(
        exception: RuntimeException,
        cache: Cache,
        key: Any,
    ) = handle(exception, cache, "get")

    override fun handleCachePutError(
        exception: RuntimeException,
        cache: Cache,
        key: Any,
        value: Any?,
    ) = handle(exception, cache, "put")

    override fun handleCacheEvictError(
        exception: RuntimeException,
        cache: Cache,
        key: Any,
    ) {
        // Feilet invalidering må aldri skjule utdaterte personopplysninger.
        throw exception
    }

    override fun handleCacheClearError(
        exception: RuntimeException,
        cache: Cache,
    ): Unit = throw exception

    private fun handle(
        feil: RuntimeException,
        cache: Cache,
        operasjon: String,
    ) {
        if (!isConnectivityFailure(feil)) throw feil
        metrikker.counter("valkey_cache_bypass_total", "cache", cache.name, "operation", operasjon).increment()
        // Ikke logg feilmeldingen: feil fra Lettuce/Redis kan inneholde kommandoer og argumentene deres.
        log.warn("Valkey unavailable for cache {} operation {}; bypassing cache", cache.name, operasjon)
    }

    private fun isConnectivityFailure(feil: Throwable): Boolean {
        var gjeldende: Throwable? = feil
        var tilkoblingsfeil = false
        while (gjeldende != null) {
            // ACL-/autentiserings-, TLS- og kommandofeil skal aldri behandles som cache-bom.
            if (gjeldende is RedisCommandExecutionException || gjeldende is SSLException) return false
            if (gjeldende is RedisConnectionFailureException ||
                gjeldende is RedisConnectionException ||
                gjeldende is RedisCommandTimeoutException ||
                gjeldende is SocketTimeoutException ||
                gjeldende is ConnectException ||
                gjeldende is UnknownHostException ||
                gjeldende is TimeoutException
            ) {
                tilkoblingsfeil = true
            }
            gjeldende = gjeldende.cause
        }
        return tilkoblingsfeil
    }
}
