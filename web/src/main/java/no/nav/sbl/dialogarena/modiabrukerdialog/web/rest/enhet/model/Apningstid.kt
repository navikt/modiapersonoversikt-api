package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet.model

data class Apningstid(
    val ukedag: String,
    val apentFra: Klokkeslett,
    val apentTil: Klokkeslett
)
