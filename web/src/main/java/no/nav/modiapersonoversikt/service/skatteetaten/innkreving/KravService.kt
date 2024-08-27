package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

import no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving.KravidentifikatorType

class KravService(
    private val skatteetatenInnkrevingClient: SkatteetatenInnkrevingClient,
) {
    fun hentKrav(kravId: KravId) =
        skatteetatenInnkrevingClient
            .getKravdetaljer(
                kravId.value,
                KravidentifikatorType.SKATTEETATENS_KRAVIDENTIFIKATOR,
            )
}
