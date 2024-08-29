package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

import no.nav.common.types.identer.Fnr

interface InnkrevingskravClient {
    fun hentInnkrevingskrav(innkrevingskravId: InnkrevingskravId): Innkrevingskrav?

    fun hentAlleInnkrevingskrav(fnr: Fnr): List<Innkrevingskrav>
}
