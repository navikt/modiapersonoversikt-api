package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

data class Kravdetaljer(
    val kravgrunnlag: Kravgrunnlag,
    val krav: List<Krav>,
)
