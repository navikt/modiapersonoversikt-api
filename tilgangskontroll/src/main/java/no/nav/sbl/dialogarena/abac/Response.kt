package no.nav.sbl.dialogarena.abac

import com.fasterxml.jackson.annotation.JsonProperty

enum class Decision {
    Permit,
    Deny,
    NotApplicable,
    Indeterminate;
}
private val applicativeDecisions = listOf(Decision.Permit, Decision.Deny)

data class AbacResponse(@JsonProperty("Response") val response: Result) {
    fun getDecision(): Decision = response.decision
    fun getBiasedDecision(bias: Decision): Decision = if (applicativeDecisions.contains(response.decision)) response.decision else bias
    fun getStatus(): Status? = response.status
    fun getObligations(): ObligationOrAdvice? = response.obligations
    fun getAssociatedAdvide(): ObligationOrAdvice? = response.obligations
    fun getPolicyIdentifierList(): PolicyIdentifier? = response.policyIdentifierList
}

data class Result(
        @JsonProperty("Decision") val decision: Decision,
        @JsonProperty("Status") val status: Status?,
        @JsonProperty("Obligations") val obligations: ObligationOrAdvice?,
        @JsonProperty("AssociatedAdvice") val associatedAdvice: ObligationOrAdvice?,
        @JsonProperty("PolicyIdentifierList") val policyIdentifierList: PolicyIdentifier?
)

data class Status(
        @JsonProperty("StatusCode") val statusCode: StatusCode?
)

data class StatusCode(
        @JsonProperty("Value") val value: String
)

data class ObligationOrAdvice(
        @JsonProperty("Id") val id: String,
        @JsonProperty("AttributeAssignment") val attributeAssignment: List<AttributeAssignment>?
)

data class AttributeAssignment(
        @JsonProperty("AttributeId") val attributeId: String,
        @JsonProperty("Value") val value: String,
        @JsonProperty("Issuer") val issuer: String?,
        @JsonProperty("DataType") val dataType: String?,
        @JsonProperty("Category") val category: String?
)

data class PolicyIdentifier(
        @JsonProperty("PolicyIdReference") val policyIdReference: List<IdReference>?,
        @JsonProperty("PolicySetIdReference") val policySetIdReference: List<IdReference>?
)

data class IdReference(
        @JsonProperty("Id") val id: String,
        @JsonProperty("Version") val version: String?
)