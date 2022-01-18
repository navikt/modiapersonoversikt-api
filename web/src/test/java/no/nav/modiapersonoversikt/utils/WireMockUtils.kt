package no.nav.modiapersonoversikt.utils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration

object WireMockUtils {
    fun withMockGateway(
        stub: WireMockServer.() -> Unit = { },
        verify: ((WireMockServer) -> Unit),
        test: (String) -> Unit
    ) {
        val config = WireMockConfiguration
            .wireMockConfig()
            .dynamicPort()
            .dynamicHttpsPort()
        val wireMockServer = WireMockServer(config)
        try {
            stub(wireMockServer)
            wireMockServer.start()
            test("http://localhost:${wireMockServer.port()}")
            verify(wireMockServer)
        } finally {
            wireMockServer.stop()
        }
    }

    fun getWithBody(statusCode: Int = 200, body: String? = null): WireMockServer.() -> Unit = {
        this.stubFor(WireMock.get(WireMock.anyUrl()).withBody(statusCode, body))
    }

    fun postWithBody(statusCode: Int = 200, body: String? = null): WireMockServer.() -> Unit = {
        this.stubFor(WireMock.post(WireMock.anyUrl()).withBody(statusCode, body))
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
