package no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.rsbac.*
import no.nav.sbl.dialogarena.rsbac.Function

class RSBACMock<T> : RSBACInstance<T> {
    override fun permit(message: String, rule: Function<T, Boolean>): RSBACInstance<T> = this
    override fun deny(message: String, rule: Function<T, Boolean>): RSBACInstance<T> = this
    override fun <S> get(auditDescriptor: Audit.AuditDescriptor<in S>, supplier: Supplier<S>): S = supplier()
    override fun getDecision(): Decision = Decision("", DecisionEnums.PERMIT)
    override fun exception(exception: Function<String, RuntimeException>): RSBACInstance<T> = this
    override fun check(policy: Policy<T>): RSBACInstance<T> = this
    override fun check(policyset: PolicySet<T>): RSBACInstance<T> = this
    override fun check(combinable: Combinable<T>): RSBACInstance<T> = this
    override fun combining(combiningAlgo: CombiningAlgo): RSBACInstance<T> = this
    override fun bias(bias: DecisionEnums): RSBACInstance<T> = this
    override fun context(): T = throw NotImplementedError("context not implemented")
}

class TilgangskontrollMock {
    companion object {
        @JvmStatic
        fun get(): Tilgangskontroll {
            val tilgangskontroll : Tilgangskontroll = mock()
            val rsbacInstance: RSBACInstance<TilgangskontrollContext> = RSBACMock()
            whenever(tilgangskontroll.check(any<Combinable<TilgangskontrollContext>>())).thenReturn(rsbacInstance)
            whenever(tilgangskontroll.check(any<Policy<TilgangskontrollContext>>())).thenReturn(rsbacInstance)
            whenever(tilgangskontroll.check(any<PolicySet<TilgangskontrollContext>>())).thenReturn(rsbacInstance)

            return tilgangskontroll
        }
    }
}
