package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.DenyCauseCode
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.TilgangsMaskinenPip
import no.nav.personoversikt.common.kabac.Decision
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext
import no.nav.personoversikt.common.kabac.utils.Key

object TilgangsMaskinenPolicy : Kabac.Policy {
    override val key = Key<Kabac.Policy>(TilgangsMaskinenPolicy)

    override fun evaluate(ctx: EvaluationContext): Decision {
        val tilgangRes = ctx.getValue(TilgangsMaskinenPip)

        return if (tilgangRes?.tilgang == true) {
            Decision.Permit()
        } else {
            Decision.Deny(
                tilgangRes?.begrunnelse ?: "Veilederen har ikke tilgang til brukeren",
                tilgangRes?.overridable.let { overridable ->
                    if (overridable == true) {
                        DenyCauseCode.TILGANGSMASKINEN_OVERRIDABLE
                    } else {
                        DenyCauseCode.TILGANGSMASKINEN
                    }
                },
            )
        }
    }
}
