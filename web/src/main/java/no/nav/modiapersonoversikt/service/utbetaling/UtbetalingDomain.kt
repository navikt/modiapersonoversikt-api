package no.nav.modiapersonoversikt.service.utbetaling

object UtbetalingDomain {
    data class Utbetaling(
        val posteringsdato: String,
        val utbetalingsdato: String?,
        val forfallsdato: String?,
        val utbetaltTil: String?,
        val erUtbetaltTilPerson: Boolean,
        val erUtbetaltTilOrganisasjon: Boolean,
        val erUtbetaltTilSamhandler: Boolean,
        val nettobelop: Double,
        val melding: String?,
        val metode: String,
        val status: String,
        val konto: String?,
        val ytelser: List<Ytelse>,
    )

    data class Ytelse(
        val type: String?,
        val ytelseskomponentListe: List<YtelseKomponent>,
        val ytelseskomponentersum: Double,
        val trekkListe: List<Trekk>,
        val trekksum: Double,
        val skattListe: List<Skatt>,
        val skattsum: Double,
        val periode: YtelsePeriode?,
        val nettobelop: Double,
        val bilagsnummer: String?,
        val arbeidsgiver: Arbeidgiver?,
    )

    data class YtelseKomponent(
        val ytelseskomponenttype: String,
        val satsbelop: Double?,
        val satstype: String?,
        val satsantall: Double?,
        val ytelseskomponentbelop: Double,
    )

    data class Trekk(
        val trekktype: String,
        val trekkbelop: Double,
        val kreditor: String?,
    )

    data class Skatt(
        val skattebelop: Double,
    )

    data class YtelsePeriode(
        val start: String,
        val slutt: String,
    )

    data class Arbeidgiver(
        val orgnr: String,
        val navn: String?,
    )
}
