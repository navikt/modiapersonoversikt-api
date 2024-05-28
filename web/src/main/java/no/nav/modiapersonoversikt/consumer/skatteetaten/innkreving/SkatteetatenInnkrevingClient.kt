package no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving

interface SkatteetatenInnkrevingClient {
    fun getKravdetaljer(
        kravidentifikator: String,
        kravidentifikatorType: KravidentifikatorType,
    ): Result<Unit>
}
