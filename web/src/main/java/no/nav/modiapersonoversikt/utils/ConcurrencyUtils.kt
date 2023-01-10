package no.nav.modiapersonoversikt.utils

import no.nav.common.auth.context.AuthContextHolderThreadLocal
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import org.slf4j.MDC
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder

object ConcurrencyUtils {
    private val authContextHolder = AuthContextHolderThreadLocal.instance()

    fun <T> makeThreadSwappable(fn: () -> T): () -> T {
        val mdc = MDC.getCopyOfContextMap()
        val context = AuthContextUtils.getContext()
        val requestAttributes: RequestAttributes? = RequestContextHolder.getRequestAttributes()
        return {
            withRequestAttributes(requestAttributes) {
                withMDC(mdc) {
                    authContextHolder.withContext(context.orElse(null), fn)
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
}
