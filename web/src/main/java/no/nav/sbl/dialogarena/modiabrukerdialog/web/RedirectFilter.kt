package no.nav.sbl.dialogarena.modiabrukerdialog.web

import no.nav.sbl.dialogarena.modiabrukerdialog.api.config.ApplicationContextProvider
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import org.slf4j.LoggerFactory
import java.net.URI
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val searchFnrRegex = Regex("\\d+")
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
            val isInternalRequest = requestURI.path.contains("/internal/")
            val isFileRequest = requestURI.path.matches(fileRequestPattern)

            if (!isFeatureEnabled || isRestRequest || isInternalRequest || isFileRequest) {
                chain.doFilter(request, response)
            } else {
                val redirectedURI = requestURI
                        .copy(
                                host = requestURI.host?.replace("modapp", "app"),
                                path = "/modiapersonoversikt",
                                query = createQuery(requestURI)
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

typealias Query = Pair<String, String?>

private fun createQuery(uri: URI): String? {
    return parseQuery(uri)
            .plus(searchFnrQueryParam(uri))
            .map(::rewriteQueryParams)
            .map { "${it.first}=${it.second}" }
            .joinToString("&")
            .let { if (it.isBlank()) null else it }
}

private fun searchFnrQueryParam(uri: URI): List<Query> {
    return searchFnrRegex.find(uri.path)?.value
            ?.let { listOf(Query("sokFnr", it)) }
            ?: emptyList()
}

private fun rewriteQueryParams(query: Query): Query =
        query.copy(
                first = if (query.first == "henvendelseid") "behandlingsid" else query.first
        )

private fun parseQuery(uri: URI): List<Query> {
    return (uri.query ?: "")
            .split("&")
            .map {
                if (it.contains("=")) it else null
            }
            .filterNotNull()
            .map {
                val data = it.split("=")
                Pair(data[0], data[1])
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
