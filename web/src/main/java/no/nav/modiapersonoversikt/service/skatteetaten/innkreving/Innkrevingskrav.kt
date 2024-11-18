package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

import no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving.api.generated.models.Kravlinje

data class Innkrevingskrav(
    val grunnlag: Grunnlag,
    val krav: List<Kravlinje>,
)
