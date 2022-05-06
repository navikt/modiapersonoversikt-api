package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.CombiningAlgorithm

object TilgangTilBrukerPolicy : Kabac.Policy by CombiningAlgorithm.denyOverride.combine(
    listOf(
        TilgangTilModiaPolicy,
        GeografiskTilgangPolicy,
        TilgangTilBrukerMedSkjermingPolicy,
        TilgangTilBrukerMedKode6Policy,
        TilgangTilBrukerMedKode7Policy
    )
)
