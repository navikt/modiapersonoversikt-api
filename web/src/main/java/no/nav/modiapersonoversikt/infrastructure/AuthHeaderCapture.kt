package no.nav.modiapersonoversikt.infrastructure

import no.nav.personoversikt.utils.StringUtils.removePrefix
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class AuthHeaderCapture : Filter {
    companion object {
        val header: ThreadLocal<String?> = ThreadLocal()
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val req = request as HttpServletRequest
        val auth: String? = req.getHeader("Authorization")?.removePrefix(prefix = "Bearer ", ignoreCase = true)
        header.set(auth)
        chain.doFilter(request, response)
        header.remove()
    }
}
