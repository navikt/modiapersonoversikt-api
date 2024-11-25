package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

import no.nav.common.types.identer.Fnr

interface InnkrevingskravClient {
    fun hentKrav(innkrevingskravId: InnkrevingskravId): Innkrevingskrav?

    fun hentAlleKravForFnr(fnr: Fnr): List<Innkrevingskrav>

    fun hentAlleKravForOrgnr(orgnr: String): List<Innkrevingskrav>
}
