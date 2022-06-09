package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll

import no.nav.modiapersonoversikt.infrastructure.kabac.Decision
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit

object TilgangskontrollMock : TilgangskontrollInstance {
    @JvmStatic
    fun get(): Tilgangskontroll = TilgangskontrollMock

    private var mockDecision: Decision = Decision.Permit()

    override fun check(policy: PolicyWithAttributes): TilgangskontrollInstance = this
    override fun <S> get(audit: Audit.AuditDescriptor<in S>, block: () -> S): S = block()
    override fun getDecision(): Decision = mockDecision

    fun <S> withDecision(decision: Decision, block: () -> S): S {
        val original = mockDecision
        mockDecision = decision
        val result = block()
        mockDecision = original
        return result
    }
}
