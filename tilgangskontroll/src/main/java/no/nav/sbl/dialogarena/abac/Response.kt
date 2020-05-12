package no.nav.sbl.dialogarena.abac

enum class Decision {
    Permit,
    Deny,
    NotApplicable,
    Indeterminate;
}

private val applicativeDecisions = listOf(Decision.Permit, Decision.Deny)

class PepException(message: String) : RuntimeException(message)

data class AbacResponse(private val response: List<Response>) {
    private val result: Response
            get() = {
                if (response.size > 1) {
                    throw PepException("Pep is giving ${response.size} responses. Only 1 is supported.")
                }
                response[0]
            }.invoke()

    fun getCause(): String {
        val associatedAdvice = result.associatedAdvice ?: emptyList();
        val attributes = associatedAdvice
                .find { advice -> advice.id == "deny_reason" }
                ?.attributeAssignment
                ?: emptyList()
        val cause = attributes.find { it.attributeId == "cause" }
        val policy = attributes.find { it.attributeId == "actual_policy" }

        return if (cause != null && cause.value == "cause-0001-manglerrolle" && policy != null) {
            policy.value
        } else if (cause != null){
            cause.value
        } else {
            "Unknown deny-reason"
        }
    }

    fun getDecision(): Decision = result.decision
    fun getBiasedDecision(bias: Decision): Decision = if (applicativeDecisions.contains(result.decision)) result.decision else bias
}

data class Response(
        val decision: Decision,
        val associatedAdvice: List<Advice>?
)

data class Advice(
        val id: String,
        val attributeAssignment: List<AttributeAssignment>?
)

data class AttributeAssignment(
        val attributeId: String,
        val value: String
)
