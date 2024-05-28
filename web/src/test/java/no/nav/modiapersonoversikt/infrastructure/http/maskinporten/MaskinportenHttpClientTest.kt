package no.nav.modiapersonoversikt.infrastructure.http.maskinporten

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.github.tomakehurst.wiremock.stubbing.Scenario
import com.nimbusds.jose.jwk.RSAKey
import no.nav.common.rest.client.RestClient
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

class MaskinportenHttpClientTest {
    companion object {
        @RegisterExtension
        val wm: WireMockExtension = WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build()
    }

    private val clientJwk = generateTestRsaJwk().toJSONString()

    private val maskinportenHttpClient = MaskinportenHttpClient(
        "${wm.baseUrl()}/token", "clientId", clientJwk, "issuer", RestClient.baseClient(), OkHttpUtils.objectMapper
    )

    @Test
    fun `get access token should return access token`() {
        wm.stubFor(
            post("/token").validateTokenRequest()
                // language=json
                .willReturnJson(
                    """
                    {
                      "access_token": "accessToken",
                      "expires_in": 3600
                    }
                    """.trimIndent()
                )
        )

        val token = maskinportenHttpClient.getAccessToken()

        assertEquals("accessToken", token)
    }

    @Test
    fun `get access token should return cached token`() {
        wm.stubFor(
            post("/token").validateTokenRequest()
                // language=json
                .willReturnJson(
                    """
                    {
                      "access_token": "accessToken",
                      "expires_in": 3600
                    }
                    """.trimIndent()
                )
        )

        val token = maskinportenHttpClient.getAccessToken()
        val cachedToken = maskinportenHttpClient.getAccessToken()

        // Verifiser at det kun ble gjort ett kall til token-endepunktet
        wm.verify(1, postRequestedFor(urlEqualTo("/token")))

        assertEquals(token, cachedToken)
    }

    @Test
    fun `get access token should return new token when cache is outdated`() {
        wm.stubFor(
            post("/token").validateTokenRequest()
                // language=json
                .willReturnJson(
                    """
                    {
                      "access_token": "accessToken",
                      "expires_in": -1
                    }
                    """.trimIndent()
                ).inScenario("getAccessToken").whenScenarioStateIs(Scenario.STARTED).willSetStateTo("cachedToken")
        )
        wm.stubFor(
            post("/token").validateTokenRequest()
                // language=json
                .willReturnJson(
                    """
                    {
                      "access_token": "newAccessToken",
                      "expires_in": 3600
                    }
                    """.trimIndent()
                ).inScenario("getAccessToken").whenScenarioStateIs("cachedToken")
        )

        val firstToken = maskinportenHttpClient.getAccessToken()
        val secondToken = maskinportenHttpClient.getAccessToken()

        // Verifiser at det ble gjort to kall til token-endepunktet
        wm.verify(2, postRequestedFor(urlEqualTo("/token")))

        assertNotEquals(firstToken, secondToken)
    }

    private fun generateTestRsaJwk(): RSAKey {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        val keyPair = keyPairGenerator.generateKeyPair()
        val privateKey = keyPair.private as RSAPrivateKey
        val publicKey = keyPair.public as RSAPublicKey

        return RSAKey.Builder(publicKey).privateKey(privateKey).keyID("test-key-id").build()
    }

    private fun MappingBuilder.validateTokenRequest(): MappingBuilder =
        withHeader("Content-Type", equalTo("application/x-www-form-urlencoded")).withFormParam(
            "grant_type", equalTo("urn:ietf:params:oauth:grant-type:jwt-bearer")
        ).withFormParam("assertion", matching(".*"))

    private fun MappingBuilder.willReturnJson(body: String): MappingBuilder = willReturn(
        aResponse().withHeader("Content-Type", "application/json").withBody(body)
    )
}