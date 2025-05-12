package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.personoversikt.common.kabac.CombiningAlgorithm
import no.nav.personoversikt.common.kabac.Kabac

object TilgangTilBrukerPolicyV2 : Kabac.Policy by CombiningAlgorithm.denyOverride.combine(
    listOf(
        TilgangTilModiaPolicy,
        TilgangsMaskinenPolicy,
    ),
)
