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

    fun getCause(): DenyCause {
        val associatedAdvice = result.associatedAdvice ?: emptyList();
        val denyReasonAttributes = associatedAdvice
                .find { advice -> advice.id == "deny_reason" }
                ?.attributeAssignment
                ?: emptyList()

        val denyReasonPolicy = denyReasonAttributes
                .find { it.attributeId == "actual_policy" }
                ?.value

        val denyCause = DenyCause
                .values()
                .find { it.policy == denyReasonPolicy }

        if (denyCause == null) {
            abacLogger.warn("Couldn't determind denyCause", associatedAdvice)
        }

        return denyCause ?: DenyCause.UNKNOWN
    }

    fun getDecision(): Decision = result.decision
    fun getBiasedDecision(bias: Decision): Decision = if (applicativeDecisions.contains(result.decision)) result.decision else bias
}

enum class DenyCause(val policy: String) {
    FP1("fp1_behandle_kode6"),
    FP2("fp2_behandle_kode7"),
    FP3("fp3_behandle_egen_ansatt"),
    FP4("fp4_geografi"),
    AD_ROLLE("modia_ad_tilganger"),
    UNKNOWN("*");
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
