package no.nav.sbl.dialogarena.abac

import com.google.gson.annotations.SerializedName

enum class Decision {
    Permit,
    Deny,
    NotApplicable,
    Indeterminate;
}
private val applicativeDecisions = listOf(Decision.Permit, Decision.Deny)

data class AbacResponse(@SerializedName("Response") val response: Result) {
    fun getDecision(): Decision = response.decision
    fun getBiasedDecision(bias: Decision): Decision = if (applicativeDecisions.contains(response.decision)) response.decision else bias
    fun getStatus(): Status? = response.status
    fun getObligations(): ObligationOrAdvice? = response.obligations
    fun getAssociatedAdvide(): ObligationOrAdvice? = response.obligations
    fun getPolicyIdentifierList(): PolicyIdentifier? = response.policyIdentifierList
}

data class Result(
        @SerializedName("Decision") val decision: Decision,
        @SerializedName("Status") val status: Status?,
        @SerializedName("Obligations") val obligations: ObligationOrAdvice?,
        @SerializedName("AssociatedAdvice") val associatedAdvice: ObligationOrAdvice?,
        @SerializedName("PolicyIdentifierList") val policyIdentifierList: PolicyIdentifier?
)

data class Status(
        @SerializedName("StatusCode") val statusCode: StatusCode?
)

data class StatusCode(
        @SerializedName("Value") val value: String,
        @SerializedName("StatusCode") val statusCode: StatusCodeValue?
)

data class StatusCodeValue(
        @SerializedName("Value") val value: String
)

data class ObligationOrAdvice(
        @SerializedName("Id") val id: String,
        @SerializedName("AttributeAssignment") val attributeAssignment: List<AttributeAssignment>?
)

data class AttributeAssignment(
        @SerializedName("AttributeId") val attributeId: String,
        @SerializedName("Value") val value: String,
        @SerializedName("Issuer") val issuer: String?,
        @SerializedName("DataType") val dataType: String?,
        @SerializedName("Category") val category: String?
)

data class PolicyIdentifier(
        @SerializedName("PolicyIdReference") val policyIdReference: List<IdReference>?,
        @SerializedName("PolicySetIdReference") val policySetIdReference: List<IdReference>?
)

data class IdReference(
        @SerializedName("Id") val id: String,
        @SerializedName("Version") val version: String?
)