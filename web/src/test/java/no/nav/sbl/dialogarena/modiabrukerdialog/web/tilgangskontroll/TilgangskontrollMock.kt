package no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rsbac.*
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rsbac.Function


class RSBACMock : RSBACInstance<TilgangskontrollContext> {
    override fun permit(message: String, rule: Function<TilgangskontrollContext, Boolean>): RSBACInstance<TilgangskontrollContext> = this
    override fun deny(message: String, rule: Function<TilgangskontrollContext, Boolean>): RSBACInstance<TilgangskontrollContext> = this
    override fun <S> get(result: Supplier<S>): S = result()
    override fun getDecision(): Decision = Decision("", DecisionEnums.PERMIT)
    override fun exception(exception: Function<String, RuntimeException>): RSBACInstance<TilgangskontrollContext> = this
    override fun check(policy: Policy<TilgangskontrollContext>): RSBACInstance<TilgangskontrollContext> = this
    override fun check(policyset: PolicySet<TilgangskontrollContext>): RSBACInstance<TilgangskontrollContext> = this
    override fun check(combinable: Combinable<TilgangskontrollContext>): RSBACInstance<TilgangskontrollContext> = this
    override fun combining(combiningAlgo: CombiningAlgo): RSBACInstance<TilgangskontrollContext> = this
    override fun bias(bias: DecisionEnums): RSBACInstance<TilgangskontrollContext> = this
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
