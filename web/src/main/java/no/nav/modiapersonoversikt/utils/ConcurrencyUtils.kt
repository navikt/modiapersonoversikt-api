package no.nav.modiapersonoversikt.utils

import kotlinx.coroutines.*
import no.nav.common.auth.context.AuthContextHolderThreadLocal
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.AuthHeaderCapture
import org.slf4j.MDC
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool.commonPool
import java.util.concurrent.TimeUnit

object ConcurrencyUtils {
    private val authContextHolder = AuthContextHolderThreadLocal.instance()

    fun <S, T> inParallel(first: () -> S, second: () -> T): Pair<S, T> {
        val firstTask = CompletableFuture.supplyAsync(makeThreadSwappable(first))
        val secondTask = CompletableFuture.supplyAsync(makeThreadSwappable(second))

        CompletableFuture.allOf(firstTask, secondTask).get()

        return Pair(firstTask.get(), secondTask.get())
    }

    fun waitForCompletion(block: (done: () -> Unit) -> Unit) {
        val future = CompletableFuture<Unit>()
        val doneFn: () -> Unit = { future.complete(Unit) }
        val swappable = makeThreadSwappable { block(doneFn) }
        commonPool().execute(swappable)
        future.get(5, TimeUnit.SECONDS)
    }

    fun <T> List<() -> T>.runInParallel(): List<T> {
        val list = this.map { makeThreadSwappable(it) }
        return runBlocking(Dispatchers.IO) {
            list.map {
                async {
                    it()
                }
            }.awaitAll()
        }
    }

    fun <T> makeThreadSwappable(fn: () -> T): () -> T {
        val mdc = MDC.getCopyOfContextMap()
        val context = AuthContextUtils.getContext()
        val requestAttributes: RequestAttributes? = RequestContextHolder.getRequestAttributes()
        val authheader = AuthHeaderCapture.header.get()
        return {
            withRequestAttributes(requestAttributes) {
                withMDC(mdc) {
                    AuthHeaderCapture.header.withValue(authheader) {
                        authContextHolder.withContext(context.orElse(null), fn)
                    }
                }
            }
        }
    }

    private fun <T> withMDC(mdcContextMap: Map<String?, String?>?, fn: () -> T): T {
        val original = MDC.getCopyOfContextMap()
        setMDC(mdcContextMap)
        val result = fn()
        setMDC(original)
        return result
    }

    private fun <T> withRequestAttributes(requestAttributes: RequestAttributes?, fn: () -> T): T {
        val original: RequestAttributes? = RequestContextHolder.getRequestAttributes()
        RequestContextHolder.setRequestAttributes(requestAttributes)
        val result = fn()
        RequestContextHolder.setRequestAttributes(original)
        return result
    }

    private fun setMDC(contextMap: Map<String?, String?>?) {
        if (contextMap == null) {
            MDC.clear()
        } else {
            MDC.setContextMap(contextMap)
        }
    }

    private fun <S, T> ThreadLocal<S>.withValue(value: S, fn: () -> T): T {
        val original = this.get()
        this.set(value)
        val result = fn()
        this.set(original)
        return result
    }
}
