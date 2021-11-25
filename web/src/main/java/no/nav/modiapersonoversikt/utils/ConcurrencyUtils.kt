package no.nav.modiapersonoversikt.utils

import no.nav.common.auth.subject.SubjectHandler
import org.slf4j.MDC
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import java.util.concurrent.CompletableFuture

object ConcurrencyUtils {
    fun <S, T> inParallel(first: () -> S, second: () -> T): Pair<S, T> {
        val firstTask = CompletableFuture.supplyAsync(makeThreadSwappable(first))
        val secondTask = CompletableFuture.supplyAsync(makeThreadSwappable(second))

        CompletableFuture.allOf(firstTask, secondTask).get()

        return Pair(firstTask.get(), secondTask.get())
    }

    private fun <T> makeThreadSwappable(fn: () -> T): () -> T {
        val mdc = MDC.getCopyOfContextMap()
        val subject = SubjectHandler.getSubject()
        val requestAttributes: RequestAttributes? = RequestContextHolder.getRequestAttributes()
        return {
            withRequestAttributes(requestAttributes) {
                withMDC(mdc) {
                    SubjectHandler.withSubject(subject.get(), fn)
                }
            }
        }
    }

    private fun <T> withMDC(mdcContextMap: Map<String?, String?>, fn: () -> T): T {
        val original = MDC.getCopyOfContextMap()
        MDC.setContextMap(mdcContextMap)
        val result = fn()
        MDC.setContextMap(original)
        return result
    }

    private fun <T> withRequestAttributes(requestAttributes: RequestAttributes?, fn: () -> T): T {
        val original: RequestAttributes? = RequestContextHolder.getRequestAttributes()
        RequestContextHolder.setRequestAttributes(requestAttributes)
        val result = fn()
        RequestContextHolder.setRequestAttributes(original)
        return result
    }
}
