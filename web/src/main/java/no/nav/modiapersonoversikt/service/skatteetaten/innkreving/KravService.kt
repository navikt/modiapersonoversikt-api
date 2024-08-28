package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

import no.nav.common.types.identer.Fnr

class KravService(
    private val skatteetatenInnkrevingClient: SkatteetatenInnkrevingClient,
) {
    fun hentKravdetaljer(kravdetaljerId: KravdetaljerId) =
        skatteetatenInnkrevingClient
            .hentKravdetaljer(
                kravdetaljerId,
            )

    fun hentAlleKravdetaljer(fnr: Fnr) =
        skatteetatenInnkrevingClient
            .hentAlleKravdetaljer(fnr)
}
