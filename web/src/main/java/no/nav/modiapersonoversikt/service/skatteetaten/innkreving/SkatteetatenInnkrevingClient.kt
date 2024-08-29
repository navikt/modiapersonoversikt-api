package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

import no.nav.common.types.identer.Fnr

interface SkatteetatenInnkrevingClient {
    fun hentKravdetaljer(kravdetaljerId: KravdetaljerId): Kravdetaljer?

    fun hentAlleKravdetaljer(fnr: Fnr): List<Kravdetaljer>
}
