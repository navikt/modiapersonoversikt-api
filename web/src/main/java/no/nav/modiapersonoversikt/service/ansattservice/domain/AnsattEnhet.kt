package no.nav.modiapersonoversikt.service.ansattservice.domain

import java.util.function.Function

data class AnsattEnhet(
    val enhetId: String,
    val enhetNavn: String,
    val status: String? = null,
) {
    fun erAktiv(): Boolean = status?.uppercase() == "AKTIV"

    companion object {
        val TIL_ENHET_ID: Function<AnsattEnhet?, String?> =
            Function { ansattEnhet: AnsattEnhet? -> ansattEnhet!!.enhetId }
    }
}
