package no.nav.modiapersonoversikt.infrastructure.http.maskinporten.json

import com.fasterxml.jackson.annotation.JsonProperty

data class MaskinportenJsonResponse(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("expires_in")
    val expiresIn: Int,
)
