package no.nav.modiapersonoversikt.infotrgd

data class CommonHistoriskUtbetaling(
    val vedtak: CommonPeriode?,
    val utbetalingsgrad: Double?,
    val utbetalingsdato: String?,
    val nettobelop: Double?,
    val bruttobelop: Double?,
    val skattetrekk: Double?,
    val arbeidsgiverNavn: String?,
    val arbeidsgiverOrgNr: String?,
    val dagsats: Double?,
    val type: String?,
    val trekk: List<CommonKreditortrekk>?,
)

data class CommonKommendeUtbetaling(
    val vedtak: CommonPeriode?,
    val utbetalingsgrad: Double?,
    val utbetalingsdato: String?,
    val bruttobelop: Double?,
    val arbeidsgiverNavn: String?,
    val arbeidsgiverOrgNr: String?,
    val arbeidsgiverKontonr: String?,
    val dagsats: Double?,
    val type: String?,
    val saksbehandler: String?,
)

data class CommonKreditortrekk(
    val kreditorsNavn: String?,
    val belop: Double?,
)

data class CommonPeriode(
    val fra: String?,
    val til: String?,
)
