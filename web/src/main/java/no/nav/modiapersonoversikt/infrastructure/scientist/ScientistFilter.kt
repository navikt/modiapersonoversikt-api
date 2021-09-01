package no.nav.modiapersonoversikt.infrastructure.scientist

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class ScientistFilter : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val req = request as HttpServletRequest
        if (req.queryString.contains("forceExperiment")) {
            Scientist.forceExperiment.set(true)
        }
        chain.doFilter(request, response)
        Scientist.forceExperiment.remove()
    }
}
