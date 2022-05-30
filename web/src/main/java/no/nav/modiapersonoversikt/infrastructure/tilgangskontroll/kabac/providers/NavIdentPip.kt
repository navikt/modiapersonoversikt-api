package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key

object NavIdentPip : Kabac.PolicyInformationPoint<NavIdent> {
    override val key = Key<NavIdent>(NavIdentPip)

    override fun provide(ctx: EvaluationContext): NavIdent {
        return ctx.getValue(AuthContextPip).requireNavIdent()
    }
}
