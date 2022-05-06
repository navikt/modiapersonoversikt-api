package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.*

internal object KanBrukeInternalPolicy : Kabac.Policy {
    override fun evaluate(ctx: EvaluationContext): Kabac.Decision {
        val ident = ctx.requireValue(NavIdentPip)
        val internalTilgang = ctx.requireValue(InternalTilgangPip)
        return if (internalTilgang.contains(ident)) {
            Kabac.Decision.Permit()
        } else {
            Kabac.Decision.Deny("Veileder har ikke tilgang til internal")
        }
    }
}
