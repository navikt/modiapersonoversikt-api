package no.nav.modiapersonoversikt.infrastructure.http.maskinporten

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.modiapersonoversikt.infrastructure.http.maskinporten.json.MaskinportenJsonResponse
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant

@Component
open class MaskinportenHttpClient(
    @Value("\${MASKINPORTEN_TOKEN_ENDPOINT}") private val maskinportenUrl: String,
    @Value("\${MASKINPORTEN_CLIENT_ID}") private val clientId: String,
    @Value("\${MASKINPORTEN_CLIENT_JWK}") private val clientJwk: String,
    @Value("\${MASKINPORTEN_ISSUER}") private val issuer: String,
    private val httpClient: OkHttpClient,
    private val objectMapper: ObjectMapper,
) : MaskinportenClient {
    private var cachedToken: CachedToken? = null

    override fun getAccessToken(): String? {
        cachedToken?.let {
            if (it.expiresAt.isAfter(Instant.now())) {
                return it.accessToken
            }
        }

        val clientAssertionJwt = createClientAssertionJwt(clientJwk, clientId, issuer, emptySet())

        val requestBody = FormBody.Builder().apply {
            add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
            add("assertion", clientAssertionJwt)
        }.build()

        val request = Request.Builder()
            .url(maskinportenUrl)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .post(requestBody)
            .build()

        return httpClient.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
                ?: return@use null

            val maskinportenJson = objectMapper.readValue(responseBody, MaskinportenJsonResponse::class.java)

            cachedToken = CachedToken(
                accessToken = maskinportenJson.accessToken,
                expiresAt = Instant.now().plusSeconds(maskinportenJson.expiresIn.toLong())
            )

            maskinportenJson.accessToken
        }
    }

    private data class CachedToken(
        val accessToken: String,
        val expiresAt: Instant,
    )
}
