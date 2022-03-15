package no.nav.modiapersonoversikt.consumer.abac

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
        get() {
            if (response.size > 1) {
                throw PepException("Pep is giving ${response.size} responses. Only 1 is supported.")
            }
            return response[0]
        }

    fun getCause(): DenyCause {
        val associatedAdvice = result.associatedAdvice ?: emptyList()
        val denyReasonAttributes = associatedAdvice
            .find { advice -> advice.id == NavAttributes.ADVICE_DENY_REASON.attributeId }
            ?.attributeAssignment
            ?: emptyList()

        val denyReasonPolicy = denyReasonAttributes
            .find { it.attributeId == NavAttributes.ADVICEOROBLIGATION_DENY_POLICY.attributeId }
            ?.value

        val denyCause = DenyCause
            .values()
            .find { it.policy.contains(denyReasonPolicy) }

        if (denyCause == null) {
            abacLogger.warn("Couldn't determind denyCause", associatedAdvice)
        }

        return denyCause ?: DenyCause.UNKNOWN
    }

    fun getDecision(): Decision = result.decision
    fun getBiasedDecision(bias: Decision): Decision = if (applicativeDecisions.contains(result.decision)) result.decision else bias
}

enum class DenyCause(vararg val policy: String) {
    FP1_KODE6(
        "fp1_behandle_kode6",
        "adressebeskyttelse_strengt_fortrolig_adresse",
        "adressebeskyttelse_strengt_fortrolig_adresse_utland"
    ),
    FP2_KODE7("fp2_behandle_kode7", "adressebeskyttelse_fortrolig_adresse"),
    FP3_EGEN_ANSATT("fp3_behandle_egen_ansatt", "skjermede_navansatte_og_familiemedlemmer"),
    FP4_GEOGRAFISK("fp4_geografi"),
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
