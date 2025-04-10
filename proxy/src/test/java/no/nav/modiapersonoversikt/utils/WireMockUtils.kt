package no.nav.modiapersonoversikt.utils

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.github.tomakehurst.wiremock.junit.Stubbing
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder
import com.github.tomakehurst.wiremock.matching.UrlPattern

object WireMockUtils {
    fun Stubbing.get(
        urlPattern: UrlPattern = WireMock.anyUrl(),
        mock: ResponseDefinitionBuilder.() -> Unit,
    ) {
        this.stubFor(
            WireMock.get(urlPattern).willReturn(aResponse().apply(mock)),
        )
    }

    fun Stubbing.verify(
        method: RequestMethod,
        urlPattern: UrlPattern,
        rules: RequestPatternBuilder.() -> Unit,
    ) {
        this.verify(
            RequestPatternBuilder(method, urlPattern).run { this.apply(rules) },
        )
    }

    fun ResponseDefinitionBuilder.status(statusCode: Int): ResponseDefinitionBuilder = this.withStatus(statusCode)

    fun ResponseDefinitionBuilder.json(body: String): ResponseDefinitionBuilder =
        this
            .withHeader("Content-Type", "application/json")
            .withBody(body)
}
