package no.nav.sbl.dialogarena.modiabrukerdialog.web

import no.nav.sbl.dialogarena.modiabrukerdialog.api.config.ApplicationContextProvider
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import org.slf4j.LoggerFactory
import javax.servlet.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RedirectFilter : Filter {
    private var unleash: UnleashService? = null

    override fun destroy() {}

    override fun init(filterConfig: FilterConfig?) {
        this.unleash = ApplicationContextProvider.context.getBean(UnleashService::class.java)
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        request as HttpServletRequest
        response as HttpServletResponse

        val shouldUseNewBackend = unleash?.isEnabled(Feature.NY_BACKEND) ?: false
        val cookie = Cookie("NY_BACKEND", shouldUseNewBackend.toString())
        cookie.path = "/"
        response.addCookie(cookie)

        chain.doFilter(request, response)
    }
}
