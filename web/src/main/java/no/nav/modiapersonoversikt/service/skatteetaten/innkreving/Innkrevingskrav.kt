package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

data class Innkrevingskrav(
    val grunnlag: Grunnlag,
    val krav: List<Krav>,
)
