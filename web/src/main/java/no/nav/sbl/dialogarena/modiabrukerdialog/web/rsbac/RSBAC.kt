package no.nav.sbl.dialogarena.modiabrukerdialog.web.rsbac

import no.nav.sbl.dialogarena.modiabrukerdialog.web.rsbac.DecisionEnums.DENY
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rsbac.DecisionEnums.PERMIT
import java.io.PrintWriter
import java.io.StringWriter

interface RSBAC<CONTEXT> {
    fun permit(message: String, rule: Function<CONTEXT, Boolean>): RSBACInstance<CONTEXT>
    fun deny(message: String, rule: Function<CONTEXT, Boolean>): RSBACInstance<CONTEXT>
    fun check(policy: Policy<CONTEXT>): RSBACInstance<CONTEXT>
    fun check(policyset: PolicySet<CONTEXT>): RSBACInstance<CONTEXT>
    fun check(combinable: Combinable<CONTEXT>): RSBACInstance<CONTEXT>
    fun combining(combiningAlgo: CombiningAlgo): RSBACInstance<CONTEXT>
    fun bias(bias: DecisionEnums): RSBACInstance<CONTEXT>
    fun exception(exception: Function<String, RuntimeException>): RSBACInstance<CONTEXT>
}

interface RSBACInstance<CONTEXT> : RSBAC<CONTEXT> {
    fun <S> get(result: Supplier<S>): S
    fun getDecision(): Decision
}

class RSBACImpl<CONTEXT>(private val context: CONTEXT, private val exception:  Function<String, RuntimeException> = { RSBACException(it) }) : RSBAC<CONTEXT> {
    override fun permit(message: String, rule: Function<CONTEXT, Boolean>) = RSBACInstanceImpl<CONTEXT, Void>(context, exception).permit(message, rule)
    override fun deny(message: String, rule: Function<CONTEXT, Boolean>) = RSBACInstanceImpl<CONTEXT, Void>(context, exception).deny(message, rule)
    override fun check(policy: Policy<CONTEXT>) = RSBACInstanceImpl<CONTEXT, Void>(context, exception).check(policy)
    override fun check(policyset: PolicySet<CONTEXT>) = RSBACInstanceImpl<CONTEXT, Void>(context, exception).check(policyset)
    override fun check(combinable: Combinable<CONTEXT>) = RSBACInstanceImpl<CONTEXT, Void>(context, exception).check(combinable)
    override fun combining(combiningAlgo: CombiningAlgo) = RSBACInstanceImpl<CONTEXT, Void>(context, exception).combining(combiningAlgo)
    override fun bias(bias: DecisionEnums) = RSBACInstanceImpl<CONTEXT, Void>(context, exception).bias(bias)
    override fun exception(exception: Function<String, RuntimeException>) = RSBACInstanceImpl<CONTEXT, Void>(context, exception).exception(exception)
}

class RSBACInstanceImpl<CONTEXT, OUTPUT>(val context: CONTEXT, var exception:  Function<String, RuntimeException>) : RSBACInstance<CONTEXT> {
    private var combiningAlgo: CombiningAlgo = CombiningAlgo.denyOverride
    private var policies: List<Combinable<CONTEXT>> = emptyList()
    private var bias = DENY

    override fun combining(combiningAlgo: CombiningAlgo): RSBACInstanceImpl<CONTEXT, OUTPUT> {
        this.combiningAlgo = combiningAlgo
        return this
    }

    override fun bias(bias: DecisionEnums): RSBACInstance<CONTEXT> {
        this.bias = bias
        return this
    }

    override fun exception(exception: Function<String, RuntimeException>): RSBACInstance<CONTEXT> {
        this.exception = exception
        return this
    }

    override fun permit(message: String, rule: Function<CONTEXT, Boolean>): RSBACInstanceImpl<CONTEXT, OUTPUT> = check(Policy(message, rule, PERMIT))
    override fun deny(message: String, rule: Function<CONTEXT, Boolean>): RSBACInstanceImpl<CONTEXT, OUTPUT> = check(Policy(message, rule, DENY))

    override fun check(policy: Policy<CONTEXT>): RSBACInstanceImpl<CONTEXT, OUTPUT> = check(policy as Combinable<CONTEXT>)
    override fun check(policyset: PolicySet<CONTEXT>): RSBACInstanceImpl<CONTEXT, OUTPUT> = check(policyset as Combinable<CONTEXT>)

    override fun check(combinable: Combinable<CONTEXT>): RSBACInstanceImpl<CONTEXT, OUTPUT> {
        this.policies = this.policies.plusElement(combinable)
        return this
    }

    override fun <S> get(result: Supplier<S>): S {
        val decision = getDecision()

        if (decision.decision == PERMIT) {
            return result.invoke()
        }

        throw this.exception(decision.message)
    }

    override fun getDecision(): Decision = try {
        combiningAlgo
                .combine(this.policies, context)
                .withBias(this.bias)
    } catch (exception: Exception) {
        val sw = StringWriter()
        exception.printStackTrace(PrintWriter(sw))
        Decision(sw.toString(), this.bias)
    }
}