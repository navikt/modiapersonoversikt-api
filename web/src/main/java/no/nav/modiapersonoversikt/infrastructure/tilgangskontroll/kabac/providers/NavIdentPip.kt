package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key

object NavIdentPip : Kabac.AttributeProvider<NavIdent> {
    override val key = Key<NavIdent>(NavIdentPip)

    override fun provide(ctx: EvaluationContext): NavIdent? {
        return ctx.requireValue(AuthContextPip).navIdent.orElse(null)
    }
}
