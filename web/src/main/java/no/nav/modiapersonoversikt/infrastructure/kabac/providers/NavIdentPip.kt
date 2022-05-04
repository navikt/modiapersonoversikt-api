package no.nav.modiapersonoversikt.infrastructure.kabac.providers

import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key

object NavIdentPip : Kabac.AttributeProvider<NavIdent> {
    override val key = Key<NavIdent>(NavIdentPip::class.java.simpleName)

    override fun provide(ctx: EvaluationContext): NavIdent? {
        val auth: AuthContextHolder = ctx.requireValue(AuthContextHolderPip)
        return auth.navIdent.orElse(null)
    }
}