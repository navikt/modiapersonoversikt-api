package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.ytelse

import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.DATOFORMAT
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.lagPeriode
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerRequest
import no.nav.sykmeldingsperioder.domain.Arbeidsforhold
import no.nav.sykmeldingsperioder.domain.UtbetalingPaVent
import no.nav.sykmeldingsperioder.domain.sykepenger.Gradering
import no.nav.sykmeldingsperioder.domain.sykepenger.Sykmelding
import no.nav.sykmeldingsperioder.domain.sykepenger.Sykmeldingsperiode
import org.joda.time.LocalDate

class SykepengerUttrekk constructor(private val sykepengerService: SykepengerServiceBi) {

    fun hent(fødselsnummer: String): Map<String, Any?> {
        val sykepenger = sykepengerService.hentSykmeldingsperioder(SykepengerRequest(LocalDate.now().minusYears(2), fødselsnummer, LocalDate.now()))

        return mapOf(
                "sykepenger" to sykepenger?.sykmeldingsperioder?.let { hentSykemeldingsperioder(it) }
        )
    }

    private fun hentSykemeldingsperioder(sykmeldingsperioder: List<Sykmeldingsperiode>): List<Map<String, Any?>> {
        return sykmeldingsperioder.map {
            mapOf(
                    "fødselsnummer" to it.fodselsnummer,
                    "sykmeldtFom" to it.sykmeldtFom?.toString(DATOFORMAT),
                    "forbrukteDager" to it.forbrukteDager,
                    "ferie1" to it.ferie1?.let { lagPeriode(it) },
                    "ferie2" to it.ferie2?.let { lagPeriode(it) },
                    "sanksjon" to it.sanksjon?.let { lagPeriode(it) },
                    "stansårsak" to it.stansarsak?.termnavn,
                    "unntakAktivitet" to it.unntakAktivitet?.termnavn,
                    "forsikring" to it.gjeldendeForsikring?.let {
                        mapOf(
                                "forsikringsordning" to it.forsikringsordning,
                                "premiegrunnlag" to it.premiegrunnlag,
                                "erGyldig" to it.erGyldig,
                                "forsikret" to it.forsikret?.let { lagPeriode(it) }
                        )
                    },
                    "sykmeldinger" to it.sykmeldinger?.let { hentSykmeldinger(it) },
                    "historiskeUtbetalinger" to it.historiskeUtbetalinger?.let { hentHistoriskeUtbetalinger(it) },
                    "kommendeUtbetalinger" to it.kommendeUtbetalinger?.let { hentKommendeUtbetalinger(it) },
                    "utbetalingerPåVent" to it.utbetalingerPaVent?.let { hentUtbetalingerPåVent(it) },
                    "bruker" to it.bruker?.ident,
                    "midlertidigStanset" to it.midlertidigStanset?.toString(DATOFORMAT),
                    "slutt" to it.slutt?.toString(DATOFORMAT),
                    "arbeidsforholdListe" to it.arbeidsforholdListe?.let { hentArbeidsgiverForhold(it) },
                    "erArbeidsgiverperiode" to it.erArbeidsgiverperiode,
                    "arbeidskategori" to it.arbeidskategori.termnavn
            )
        }
    }

    private fun hentArbeidsgiverForhold(arbeidsgiverforholdListe: List<Arbeidsforhold>): List<Map<String, Any?>> {
        return arbeidsgiverforholdListe.map {
            mapOf(
                    "arbeidsgiverNavn" to it.arbeidsgiverNavn,
                    "arbeidsgiverKontonr" to it.arbeidsgiverKontonr,
                    "inntektsperiode" to it.inntektsperiode.termnavn,
                    "inntektForPerioden" to it.inntektForPerioden,
                    "refusjonTom" to it.refusjonTom.toString(DATOFORMAT),
                    "refusjonstype" to it.refusjonstype.termnavn,
                    "sykepengerFom" to it.sykepengerFom.toString(DATOFORMAT)
            );
        }
    }

    private fun hentSykmeldinger(sykmeldinger: List<Sykmelding>): List<Map<String, Any?>> {
        return sykmeldinger.map {
            mapOf(
                    "sykmelder" to it.sykmelder,
                    "behandlet" to it.behandlet?.toString(DATOFORMAT),
                    "sykmeldt" to it.sykmeldt?.let { lagPeriode(it) },
                    "sykmeldingsgrad" to it.sykmeldingsgrad,
                    "gjelderYrkesskade" to it.gjelderYrkesskade?.let {
                        mapOf(
                                "yrkesskadeart" to it.yrkesskadeart?.termnavn,
                                "skadet" to it.skadet?.toString(DATOFORMAT),
                                "vedtatt" to it.vedtatt?.toString(DATOFORMAT)
                        )
                    },
                    "gradAvSykmeldingListe" to it.gradAvSykmeldingListe?.let { hentGraderinger(it) }
            )
        }
    }

    private fun hentGraderinger(graderinger: List<Gradering>): List<Map<String, Any?>> {
        return graderinger.map {
            mapOf(
                    "gradert" to it.gradert?.let { lagPeriode(it) },
                    "sykmeldingsgrad" to it.sykmeldingsgrad
            )
        }
    }

    private fun hentUtbetalingerPåVent(utbetalingerPåVent: List<UtbetalingPaVent>): List<Map<String, Any?>> {
        return utbetalingerPåVent.map {
            mapOf(
                    "vedtak" to it.vedtak?.let { lagPeriode(it) },
                    "utbetalingsgrad" to it.utbetalingsgrad,
                    "oppgjørstype" to it.oppgjoerstype?.termnavn,
                    "arbeidskategori" to it.arbeidskategori?.termnavn,
                    "stansårsak" to it.stansaarsak?.termnavn,
                    "ferie1" to it.ferie1?.let { lagPeriode(it) },
                    "ferie2" to it.ferie2?.let { lagPeriode(it) },
                    "sanksjon" to it.sanksjon?.let { lagPeriode(it) },
                    "sykmeldt" to it.sykmeldt?.let { lagPeriode(it) }
            )
        }
    }

}