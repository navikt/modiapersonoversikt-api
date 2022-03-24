package no.nav.modiapersonoversikt.utils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.matching.UrlPattern

object WireMockUtils {
    fun withMockGateway(
        stub: Array<WireMockServer.() -> Unit> = emptyArray(),
        verify: ((WireMockServer) -> Unit),
        test: (String) -> Unit
    ) {
        val config = WireMockConfiguration
            .wireMockConfig()
            .dynamicPort()
            .dynamicHttpsPort()
        val wireMockServer = WireMockServer(config)
        try {
            stub.forEach { it(wireMockServer) }
            wireMockServer.start()
            test("http://localhost:${wireMockServer.port()}")
            verify(wireMockServer)
        } finally {
            wireMockServer.stop()
        }
    }

    fun withMockGateway(
        stub: WireMockServer.() -> Unit = { },
        verify: ((WireMockServer) -> Unit),
        test: (String) -> Unit
    ) = withMockGateway(
        stub = arrayOf(stub),
        verify = verify,
        test = test
    )

    fun getWithBody(statusCode: Int = 200, url: UrlPattern? = WireMock.anyUrl(), body: String? = null): WireMockServer.() -> Unit = {
        this.stubFor(WireMock.get(url).withBody(statusCode, body))
    }

    fun postWithBody(statusCode: Int = 200, url: UrlPattern? = WireMock.anyUrl(), body: String? = null): WireMockServer.() -> Unit = {
        this.stubFor(WireMock.post(url).withBody(statusCode, body))
    }

    private fun MappingBuilder.withBody(statusCode: Int = 200, body: String? = null): MappingBuilder {
        this.willReturn(
            WireMock.aResponse()
                .withStatus(statusCode)
                .withHeader("Content-Type", "application/json")
                .withBody(body)
        )
        return this
    }
}
