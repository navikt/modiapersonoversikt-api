package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac

object PublicPolicies {
    val tilgangTilModia: Kabac.Policy = TilgangTilModiaPolicy
    val tilgangTilBruker: Kabac.Policy = TilgangTilBrukerPolicy
    val tilgangTilTema: Kabac.Policy = TilgangTilTemaPolicy
    val henvendelseTilhorerBruker: Kabac.Policy = HenvendelseTilhorerBrukerPolicy
    val kanBrukeInternal: Kabac.Policy = KanBrukeInternalPolicy
}
