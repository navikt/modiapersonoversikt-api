package no.nav.modiapersonoversikt.rest.ytelse

data class ForeldrepengerResponse(
    val foreldrepenger: List<Foreldrepenger>?,
)

data class Foreldrepenger(
    val forelder: String?,
    val andreForeldersFnr: String?,
    val antallBarn: Int?,
    val barnetsFodselsdato: String?,
    val dekningsgrad: Double?,
    val fedrekvoteTom: String?,
    val modrekvoteTom: String?,
    val foreldrepengetype: String?,
    val graderingsdager: Int?,
    val restDager: Int?,
    val rettighetFom: String?,
    val eldsteIdDato: String?,
    val foreldreAvSammeKjonn: String?,
    val periode: List<ForeldrepengePeriode>?,
    val slutt: String?,
    val arbeidsforhold: List<ForeldrepengerArbeidsforhold>?,
    val erArbeidsgiverperiode: Boolean?,
    val arbeidskategori: String?,
    val omsorgsovertakelse: String?,
    val termin: String?,
)

data class ForeldrepengerArbeidsforhold(
    val arbeidsgiverNavn: String?,
    val arbeidsgiverKontonr: String?,
    val inntektsperiode: String?,
    val inntektForPerioden: Double?,
    val sykepengerFom: String?,
    val refusjonTom: String?,
    val refusjonstype: String?,
)

data class ForeldrepengePeriode(
    val fodselsnummer: String?,
    val harAleneomsorgFar: Boolean?,
    val harAleneomsorgMor: Boolean?,
    val arbeidsprosentMor: Double?,
    val avslagsaarsak: String?,
    val avslaatt: String?,
    val disponibelGradering: Double?,
    val erFedrekvote: Boolean?,
    val forskyvelsesaarsak1: String?,
    val forskyvelsesperiode1: CommonPeriode?,
    val forskyvelsesaarsak2: String?,
    val forskyvelsesperiode2: CommonPeriode?,
    val foreldrepengerFom: String?,
    val midlertidigStansDato: String?,
    val erModrekvote: Boolean?,
    val morSituasjon: String?,
    val rettTilFedrekvote: String?,
    val rettTilModrekvote: String?,
    val stansaarsak: String?,
    val historiskeUtbetalinger: List<CommonHistoriskUtbetaling>?,
    val kommendeUtbetalinger: List<CommonKommendeUtbetaling>?,
)
