package no.nav.modiapersonoversikt.rest.utbetaling

class UtbetalingDTO(
    val posteringsdato: String,
    val utbetalingsdato: String?,
    val forfallsdato: String?,
    val utbetaltTil: String,
    val erUtbetaltTilPerson: Boolean,
    val erUtbetaltTilOrganisasjon: Boolean,
    val erUtbetaltTilSamhandler: Boolean,
    val nettobelop: Double,
    val melding: String?,
    val metode: String,
    val status: String,
    val konto: String?,
    val ytelser: List<YtelseDTO>,
)

class YtelseDTO(
    val type: String?,
    val ytelseskomponentListe: List<YtelseKomponentDTO>,
    val ytelseskomponentersum: Double,
    val trekkListe: List<TrekkDTO>,
    val trekksum: Double,
    val skattListe: List<SkattDTO>,
    val skattsum: Double,
    val periode: YtelsePeriodeDTO,
    val nettobelop: Double,
    val bilagsnummer: String?,
    val arbeidsgiver: ArbeidgiverDTO?,
)

class YtelseKomponentDTO(
    val ytelseskomponenttype: String,
    val satsbelop: Double?,
    val satstype: String?,
    val satsantall: Double?,
    val ytelseskomponentbelop: Double,
)
class TrekkDTO(
    val trekktype: String,
    val trekkbelop: Double,
    val kreditor: String?,
)
class SkattDTO(
    val skattebelop: Double,
)
class YtelsePeriodeDTO(
    val start: String,
    val slutt: String,
)
class ArbeidgiverDTO(
    val orgnr: String,
    val navn: String?,
)