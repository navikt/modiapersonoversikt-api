package no.nav.modiapersonoversikt.infrastructure.http.maskinporten

interface MaskinportenClient {
    fun getAccessToken(): String?
}