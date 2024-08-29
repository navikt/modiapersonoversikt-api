package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

import no.nav.common.types.identer.Fnr

class InnkrevingskravService(
    private val innkrevingskravClient: InnkrevingskravClient,
) {
    fun hentInnkrevingskrav(innkrevingskravId: InnkrevingskravId) =
        innkrevingskravClient
            .hentInnkrevingskrav(
                innkrevingskravId,
            )

    fun hentAlleInnkrevingskrav(fnr: Fnr) =
        innkrevingskravClient
            .hentAlleInnkrevingskrav(fnr)
}
