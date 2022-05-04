package no.nav.modiapersonoversikt.infrastructure.kabac.providers

import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.AuthContextHolderThreadLocal
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key

object AuthContextHolderPip : Kabac.AttributeProvider<AuthContextHolder> {
    override val key = Key<AuthContextHolder>(AuthContextHolderPip::class.java.simpleName)

    override fun provide(ctx: EvaluationContext): AuthContextHolder {
        return AuthContextHolderThreadLocal.instance()
    }
}