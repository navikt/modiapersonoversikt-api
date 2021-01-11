package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

private const val INGEN_BEHANDLINGSID = "[INGEN BEHANDLINGSKJEDEID]"

class EnhetIkkeSatt(message: String) : IllegalArgumentException(message)

@ExperimentalContracts
fun requireKnyttTilSakParametereNotNullOrBlank(sak: Sak?, behandlingskjede: String?, fnr: String?, enhet: String?): Unit {
    contract {
        returns() implies (sak != null && behandlingskjede != null && fnr != null && enhet != null)
    }

    requireNotNull(sak) {
        "Sak-parameter må være tilstede for å kunne knytte behandlingskjede ${behandlingskjede ?: INGEN_BEHANDLINGSID} til sak."
    }
    val saksId = requireNotNullOrBlank(sak.saksId) {
        "SaksId-parameter må være tilstede for å kunne knytte behandlingskjede ${behandlingskjede ?: INGEN_BEHANDLINGSID} til sak."
    }
    requireNotNullOrBlank(behandlingskjede) {
        "Behandlingskjede-parameter må være tilstede for å kunne knytte behandlingskjeden til sak $saksId."
    }
    requireNotNullOrBlank(fnr) {
        "Fnr-parameter må være tilstede for å kunne knytte behandlingskjede $behandlingskjede til sak $saksId."
    }
    requireNotNullOrBlank(enhet, { EnhetIkkeSatt(it) }) {
        "Enhet-parameter må være tilstede for å kunne knytte behandlingskjede $behandlingskjede til sak $saksId."
    }
}

@ExperimentalContracts
internal fun requireFnrNotNullOrBlank(fnr: String?): String {
    contract { returns() implies (fnr != null) }
    return requireNotNullOrBlank(fnr) { "Fnr-parameter må være tilstede." }
}

@ExperimentalContracts
private inline fun requireNotNullOrBlank(value: String?, lazyMessage: () -> String): String {
    contract {
        returns() implies (value != null)
    }
    return requireNotNullOrBlank(value, { IllegalArgumentException(it) }, lazyMessage)
}

@ExperimentalContracts
private inline fun requireNotNullOrBlank(value: String?, exception: (String) -> Exception = { IllegalArgumentException(it) }, lazyMessage: () -> String): String {
    contract {
        returns() implies (value != null)
    }

    if (value.isNullOrBlank()) {
        val message = lazyMessage()
        throw exception(message)
    } else {
        return value
    }
}
