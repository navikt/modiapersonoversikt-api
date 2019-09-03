package no.nav.sbl.dialogarena.modiabrukerdialog.web.rsbac

typealias Supplier<T> = () -> T
typealias Function<S, T> = (s: S) -> T
typealias Rule<CONTEXT> = Function<CONTEXT, DecisionEnums>

class RSBACException(override val message: String) : RuntimeException(message)

enum class DecisionEnums {
    PERMIT, DENY, NOT_APPLICABLE;

    fun negate(): DecisionEnums {
        return when (this) {
            PERMIT -> DENY
            DENY -> PERMIT
            NOT_APPLICABLE -> NOT_APPLICABLE
        }
    }

    fun withBias(bias: DecisionEnums): DecisionEnums {
        return when (this) {
            NOT_APPLICABLE -> bias
            else -> this
        }
    }

    fun isApplicable() = this == PERMIT || this == DENY
}

data class Decision(val message: String, val decision: DecisionEnums) {
    fun withBias(bias: DecisionEnums) = Decision(this.message, this.decision.withBias(bias))
}

class PolicySet<CONTEXT>(
        val combining: CombiningAlgo = CombiningAlgo.denyOverride,
        val policies: List<Combinable<CONTEXT>>
) : Combinable<CONTEXT> {
    private var result: Decision? = null

    override fun getMessage(): String {
        return result!!.message
    }

    override fun invoke(context: CONTEXT): DecisionEnums {
        result = this.combining.combine(this.policies, context)
        return result!!.decision
    }
}

class Policy<CONTEXT> : Combinable<CONTEXT> {
    private val message: String
    private val rule: Rule<CONTEXT>

    constructor(message: String, rule: Rule<CONTEXT>) {
        this.message = message
        this.rule = rule
    }

    constructor(message: String, rule: Function<CONTEXT, Boolean>, effect: DecisionEnums) {
        this.message = message
        this.rule = { if (rule.invoke(it)) effect else effect.negate() }
    }

    override fun getMessage(): String {
        return this.message
    }

    override fun invoke(context: CONTEXT): DecisionEnums {
        return this.rule.invoke(context)
    }
}