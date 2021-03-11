package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.arbeidsfordeling

data class ArbeidskritereieFordelingSkjermet(
    val behandlingstema: String?,
    val behandlingstype: String?,
    val diskresjonskode: String?,
    val enhetsnummer: String?,
    val geografiskOmraade: String,
    val oppgavetype: String,
    val skjermet: Boolean,
    val tema: String,
    val temagruppe: String?

)
