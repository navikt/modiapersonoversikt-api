package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.personoversikt.common.kabac.CombiningAlgorithm
import no.nav.personoversikt.common.kabac.Kabac

object TilgangTilBrukerPolicy : Kabac.Policy by CombiningAlgorithm.denyOverride.combine(
    listOf(
        TilgangTilModiaPolicy,
        GeografiskTilgangPolicy,
        TilgangTilBrukerMedSkjermingPolicy,
        TilgangTilBrukerMedKode6Policy,
        TilgangTilBrukerMedKode7Policy,
    ),
)
