package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.kabac.Decision
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.*

internal object KanBrukeInternalPolicy : Kabac.Policy {
    override val key = Key<Kabac.Policy>(KanBrukeInternalPolicy)
    override fun evaluate(ctx: EvaluationContext): Decision {
        val ident = checkNotNull(ctx.getValue(NavIdentPip)) {
            "Kan ikke avgjøre tilgang til internal uten navident"
        }
        val internalTilgang = ctx.getValue(InternalTilgangPip)
        return if (internalTilgang.contains(ident)) {
            Decision.Permit()
        } else {
            Decision.Deny("Veileder har ikke tilgang til internal")
        }
    }
}
