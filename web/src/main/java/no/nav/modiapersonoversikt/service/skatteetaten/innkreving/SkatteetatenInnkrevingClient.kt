package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

import no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving.KravidentifikatorType

interface SkatteetatenInnkrevingClient {
    fun getKravdetaljer(
        kravidentifikator: String,
        kravidentifikatorType: KravidentifikatorType,
    ): Kravdetaljer?
}
