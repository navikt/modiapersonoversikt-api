package no.nav.modiapersonoversikt.rest.ytelse

import java.math.BigDecimal

data class PleiepengerResponse(
    val pleiepenger: List<Pleiepenger>?,
)

data class Pleiepenger(
    val barnet: String?,
    val omsorgsperson: String?,
    val andreOmsorgsperson: String?,
    val restDagerFomIMorgen: Int?,
    val forbrukteDagerTomIDag: Int?,
    val pleiepengedager: Int?,
    val restDagerAnvist: Int?,
    val perioder: List<PleiepengerPeriode>?,
)

data class PleiepengerPeriode(
    val fom: String?,
    val antallPleiepengedager: Int?,
    val arbeidsforhold: List<PleiepengerArbeidsforhold>?,
    val vedtak: List<PleiepengerVedtak>?,
)

data class PleiepengerVedtak(
    val periode: PleiepengerVedtakPeriode?,
    val kompensasjonsgrad: Int?,
    val utbetalingsgrad: Int?,
    val anvistUtbetaling: String?,
    val bruttobelop: BigDecimal?,
    val dagsats: BigDecimal?,
    val pleiepengegrad: Int?,
)

data class PleiepengerArbeidsforhold(
    val arbeidsgiverNavn: String?,
    val arbeidsgiverKontonr: String?,
    val inntektsperiode: String?,
    val refusjonTom: String?,
    val refusjonstype: String?,
    val arbeidsgiverOrgnr: String?,
    val arbeidskategori: String?,
    val inntektForPerioden: BigDecimal?,
)

data class PleiepengerVedtakPeriode(
    val fom: String?,
    val tom: String?,
)
