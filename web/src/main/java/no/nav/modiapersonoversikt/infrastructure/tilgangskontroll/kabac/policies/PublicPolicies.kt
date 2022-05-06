package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.CombiningAlgorithm.Companion.denyOverride

object PublicPolicies {
    val tilgangTilModia: Kabac.Policy = TilgangTilModiaPolicy
    val tilgangTilBruker: Kabac.Policy = denyOverride.combine(
        listOf(
            TilgangTilModiaPolicy,
            GeografiskTilgangPolicy,
            TilgangTilBrukerMedSkjermingPolicy,
            TilgangTilBrukerMedKode6Policy,
            TilgangTilBrukerMedKode7Policy,
        )
    )

    val tilgangTilTema: Kabac.Policy = TilgangTilTemaPolicy
    val henvendelseTilhorerBruker: Kabac.Policy = HenvendelseTilhorerBrukerPolicy
    val kanBrukeInternal: Kabac.Policy = KanBrukeInternalPolicy
}
