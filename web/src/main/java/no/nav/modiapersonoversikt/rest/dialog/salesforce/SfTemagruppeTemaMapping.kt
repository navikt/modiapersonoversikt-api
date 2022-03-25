package no.nav.modiapersonoversikt.rest.dialog.salesforce

import no.nav.modiapersonoversikt.commondomain.Temagruppe
import no.nav.modiapersonoversikt.commondomain.TemagruppeTemaMapping

object SfTemagruppeTemaMapping {
    private val legacyMapping = HashMap(TemagruppeTemaMapping.TEMA_TEMAGRUPPE_MAPPING)

    private val enkeltTemaOverstyring = mapOf(
        "UFO" to Temagruppe.PENS.name
    )

    private val sfGodkjenteTemagrupper = listOf(
        Temagruppe.ARBD,
        Temagruppe.PENS,
        Temagruppe.FMLI,
        Temagruppe.HELSE,
        Temagruppe.HJLPM,
        Temagruppe.OVRG
    ).map { it.name }

    private val sfMapping = legacyMapping
        .apply { putAll(enkeltTemaOverstyring) }
        .mapValues {
            if (it.value in sfGodkjenteTemagrupper) {
                it.value
            } else {
                Temagruppe.OVRG.name
            }
        }

    fun hentTemagruppeForTema(temaKode: String): String {
        return sfMapping[temaKode] ?: TemagruppeTemaMapping.TEMA_UTEN_TEMAGRUPPE.name
    }
}
