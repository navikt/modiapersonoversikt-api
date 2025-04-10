package no.nav.modiapersonoversikt.infotrgd.sykepenger

import no.nav.modiapersonoversikt.infotrgd.CommonHistoriskUtbetaling
import no.nav.modiapersonoversikt.infotrgd.CommonKommendeUtbetaling
import no.nav.modiapersonoversikt.infotrgd.CommonPeriode

data class SykepengerResponse(
    val sykepenger: List<Sykepenger>?,
)

data class Sykepenger(
    val fodselsnummer: String?,
    val sykmeldtFom: String?,
    val forbrukteDager: Int?,
    val ferie1: CommonPeriode?,
    val ferie2: CommonPeriode?,
    val sanksjon: CommonPeriode?,
    val stansaarsak: String?,
    val unntakAktivitet: String?,
    val forsikring: GjeldendeForsikring?,
    val sykmeldinger: List<SykmeldingItem>?,
    val historiskeUtbetalinger: List<CommonHistoriskUtbetaling>?,
    val kommendeUtbetalinger: List<CommonKommendeUtbetaling>?,
    val utbetalingerPaaVent: List<SykmeldingUtbetalingPaVent>?,
    val bruker: String?,
    val midlertidigStanset: String?,
    val slutt: String?,
    val arbeidsforholdListe: List<SykmeldingArbeidsforhold>?,
    val erArbeidsgiverperiode: Boolean?,
    val arbeidskategori: String?,
)

data class SykmeldingItem(
    val sykmelder: String?,
    val behandlet: String?,
    val sykmeldt: CommonPeriode?,
    val sykmeldingsgrad: Double?,
    val gjelderYrkesskade: GjelderYrkesskade?,
    val gradAvSykmeldingListe: List<GradAvSykmelding>?,
)

data class GjelderYrkesskade(
    val yrkesskadeart: String?,
    val skadet: String?,
    val vedtatt: String?,
)

data class GjeldendeForsikring(
    val forsikringsordning: String?,
    val premiegrunnlag: Double?,
    val erGyldig: Boolean?,
    val forsikret: CommonPeriode?,
)

data class GradAvSykmelding(
    val gradert: CommonPeriode?,
    val sykmeldingsgrad: Double?,
)

data class SykmeldingArbeidsforhold(
    val arbeidsgiverNavn: String?,
    val arbeidsgiverKontonr: String?,
    val inntektsperiode: String?,
    val inntektForPerioden: Double?,
    val refusjonTom: String?,
    val refusjonstype: String?,
    val sykepengerFom: String?,
)

data class SykmeldingUtbetalingPaVent(
    val vedtak: CommonPeriode?,
    val utbetalingsgrad: Double?,
    val oppgjorstype: String?,
    val arbeidskategori: String?,
    val stansaarsak: String?,
    val ferie1: CommonPeriode?,
    val ferie2: CommonPeriode?,
    val sanksjon: CommonPeriode?,
    val sykmeldt: CommonPeriode?,
)
