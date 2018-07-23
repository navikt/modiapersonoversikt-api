package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.oppgave

data class LeggTilbakeRequest(
    val temagruppe: String,
    val beskrivelse: String? = null
)
