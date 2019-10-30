package no.nav.sbl.dialogarena.modiabrukerdialog.web

import com.nhaarman.mockito_kotlin.*
import no.nav.sbl.dialogarena.modiabrukerdialog.api.config.ApplicationContextProvider
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.sbl.dialogarena.modiabrukerdialog.web.RedirectFilterTest.Companion.filter
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationContext
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private typealias RequestContext = Triple<HttpServletRequest, HttpServletResponse, FilterChain>

private const val fnr = "12345678910"
private const val oppgaveid = "123456789"
private const val behandlingsid = "ACDC1ABBA"

internal class RedirectFilterTest {
    companion object {
        val filter = RedirectFilter()

        @BeforeAll
        @JvmStatic
        fun setup() {
            val mock = mock<ApplicationContext>()
            val unleashMock = mock<UnleashService>()
            ApplicationContextProvider.context = mock

            whenever(mock.getBean(any<Class<*>>())).thenReturn(unleashMock)
            whenever(unleashMock.isEnabled(any<Feature>())).thenReturn(true)

            filter.init(null)
        }
    }


    @Test
    fun `redirect av oppstarts-url`() {
        callsTo("https://modapp-q1.adeo.no/modiabrukerdialog")
                .isRedirectedTo("https://app-q1.adeo.no/modiapersonoversikt")
        callsTo("https://modapp-q1.adeo.no/modiabrukerdialog/")
                .isRedirectedTo("https://app-q1.adeo.no/modiapersonoversikt")
        callsTo("https://modapp-q1.adeo.no/modiabrukerdialog?")
                .isRedirectedTo("https://app-q1.adeo.no/modiapersonoversikt")
        callsTo("https://modapp-q1.adeo.no/modiabrukerdialog?4")
                .isRedirectedTo("https://app-q1.adeo.no/modiapersonoversikt")
        callsTo("https://modapp-q1.adeo.no/modiabrukerdialog/?4")
                .isRedirectedTo("https://app-q1.adeo.no/modiapersonoversikt")
    }

    @Test
    fun `redirect av person-url`() {
        callsTo("https://modapp-q1.adeo.no/modiabrukerdialog/person/$fnr")
                .isRedirectedTo("https://app-q1.adeo.no/modiapersonoversikt/person/$fnr/")
        callsTo("https://modapp-q1.adeo.no/modiabrukerdialog/person/$fnr/")
                .isRedirectedTo("https://app-q1.adeo.no/modiapersonoversikt/person/$fnr/")
        callsTo("https://modapp-q1.adeo.no/modiabrukerdialog/person/$fnr?")
                .isRedirectedTo("https://app-q1.adeo.no/modiapersonoversikt/person/$fnr/")
        callsTo("https://modapp-q1.adeo.no/modiabrukerdialog/person/$fnr?4")
                .isRedirectedTo("https://app-q1.adeo.no/modiapersonoversikt/person/$fnr/")
        callsTo("https://modapp-q1.adeo.no/modiabrukerdialog/person/$fnr/?4")
                .isRedirectedTo("https://app-q1.adeo.no/modiapersonoversikt/person/$fnr/")
    }

    @Test
    fun `redirect av oppgave-url`() {
        callsTo("https://modapp-q1.adeo.no/modiabrukerdialog/person/$fnr/?oppgaveid=$oppgaveid&behandlingsid=$behandlingsid")
                .isRedirectedTo("https://app-q1.adeo.no/modiapersonoversikt/person/$fnr/meldinger?oppgaveid=$oppgaveid&behandlingsid=$behandlingsid")
        callsTo("https://modapp-q1.adeo.no/modiabrukerdialog/person/$fnr/?behandlingsid=$behandlingsid")
                .isRedirectedTo("https://app-q1.adeo.no/modiapersonoversikt/person/$fnr/meldinger?behandlingsid=$behandlingsid")
        callsTo("https://modapp-q1.adeo.no/modiabrukerdialog/person/$fnr/?4&behandlingsid=$behandlingsid")
                .isRedirectedTo("https://app-q1.adeo.no/modiapersonoversikt/person/$fnr/meldinger?behandlingsid=$behandlingsid")
    }

    @Test
    fun `ved exceptions fra redirect`() {
        redirectionFailures()
                .isUsingDefaultFilterChain()
    }

    @Test
    fun `ved kall til rest-apiet`() {
        callsTo("https://modapp-q1.adeo.no/modiabrukerdialog/rest/hode/me")
                .isUsingDefaultFilterChain()
    }

    @Test
    fun `ved kall for a hente fil-ressurser`() {
        callsTo("https://modapp-q1.adeo.no/modiabrukerdialog/ikoner/person.svg")
                .isUsingDefaultFilterChain()
    }

}

private fun callsTo(url: String): RequestContext {
    return RequestContext(
            createRequest(url),
            mock(),
            mock()
    ).exec()
}

private fun redirectionFailures(): RequestContext {
    val req = mock<HttpServletRequest>()
    whenever(req.requestURL).thenReturn(null)

    return RequestContext(
            req,
            mock(),
            mock()
    ).exec()
}

private fun RequestContext.isRedirectedTo(url: String) {
    verify(this.third, never()).doFilter(any(), any())
    verify(this.second, times(1)).sendRedirect(url)
}

private fun RequestContext.isUsingDefaultFilterChain() {
    verify(this.third, times(1)).doFilter(any(), any())
    verify(this.second, never()).sendRedirect(any())
}

private fun RequestContext.exec(): RequestContext {
    filter.doFilter(this.first, this.second, this.third)
    return this
}

private fun createRequest(url: String): HttpServletRequest {
    val fragments = url.split("?")
    val requestURL = fragments[0]
    val queryString = if (fragments.size > 1) fragments[1] else null

    val req = mock<HttpServletRequest>()
    whenever(req.requestURL).thenReturn(StringBuffer(requestURL))
    whenever(req.queryString).thenReturn(queryString)

    return req
}