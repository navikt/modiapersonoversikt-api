package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.AuthContextHolderThreadLocal
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key

object AuthContextPip : Kabac.AttributeProvider<AuthContextHolder> {
    private val contextHolder = AuthContextHolderThreadLocal.instance()

    override val key = Key<AuthContextHolder>(AuthContextPip)
    override fun provide(ctx: EvaluationContext): AuthContextHolder = contextHolder
}
