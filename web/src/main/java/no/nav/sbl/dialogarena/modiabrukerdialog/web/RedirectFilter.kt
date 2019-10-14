package no.nav.sbl.dialogarena.modiabrukerdialog.web

import no.nav.sbl.dialogarena.modiabrukerdialog.api.config.ApplicationContextProvider
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.sbl.util.StringUtils
import org.slf4j.LoggerFactory
import java.net.URI
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val fnrRegex = Regex("\\d{11}")
private val fileRequestPattern = Regex("^(.+\\.\\w{1,4})$")

class RedirectFilter : Filter {
    companion object {
        private val logger = LoggerFactory.getLogger(RedirectFilter::class.java)
    }

    private var unleash: UnleashService? = null

    override fun destroy() {}

    override fun init(filterConfig: FilterConfig?) {
        this.unleash = ApplicationContextProvider.context.getBean(UnleashService::class.java)
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        try {
            request as HttpServletRequest
            response as HttpServletResponse
            val requestURI = URI(getUrl(request))

            val isFeatureEnabled = unleash?.isEnabled(Feature.NY_FRONTEND) ?: false
            val isRestRequest = requestURI.path.contains("/rest/")
            val isFileRequest = requestURI.path.matches(fileRequestPattern)

            if (!isFeatureEnabled || isRestRequest || isFileRequest) {
                chain.doFilter(request, response)
            } else {
                val redirectedURI = requestURI
                        .copy(
                                host = requestURI.host?.replace("modapp", "app"),
                                path = guessPath(requestURI),
                                query = rewriteQuery(requestURI.query)
                        )
                response.sendRedirect(redirectedURI.toString())
            }
        } catch (e: Exception) {
            logger.error("Redirect to new frontend failed. [requestURI: $request]", e)
            chain.doFilter(request, response)
        }
    }
}

private fun getUrl(request: HttpServletRequest): String {
    val params = request.queryString
            ?.let { "?$it" }
            ?: ""
    return request.requestURL.toString() + params
}

private fun rewriteQuery(query: String?): String? {
    val cleanedQuery = cleanQuery(query) ?: return null

    return cleanedQuery
            .split("&")
            .map{
                val data = it.split("=")
                val key = if (data[0].equals("henvendelseid")) "behandlingsid" else data[0]
                listOf(key, data[1]).joinToString("=")
            }
            .joinToString("&")
}

private fun cleanQuery(query: String?): String? {
    return query
            ?.split("&")
            ?.map {
                if (it.contains("=")) it else null
            }
            ?.filter { it != null }
            ?.joinToString("&")
            .let { if (it == "") null else it }
}

private fun guessPath(uri: URI): String {
    val fnr = fnrRegex.find(uri.path)?.value
    val hasQueryParams = StringUtils.notNullOrEmpty(cleanQuery(uri.query))
    val lamell = if (hasQueryParams) "meldinger" else ""

    return if (fnr == null) {
        "/modiapersonoversikt"
    } else {
        "/modiapersonoversikt/person/$fnr/$lamell"
    }
}

private fun URI.copy(
        scheme: String? = this.scheme,
        userInfo: String? = this.userInfo,
        host: String? = this.host,
        port: Int = this.port,
        path: String = this.path,
        query: String? = this.query,
        fragment: String? = this.fragment
): URI = URI(scheme, userInfo, host, port, path, query, fragment)
