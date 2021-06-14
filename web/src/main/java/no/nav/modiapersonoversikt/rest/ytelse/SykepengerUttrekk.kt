package no.nav.modiapersonoversikt.rest.ytelse

import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerRequest
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.Arbeidsforhold
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.UtbetalingPaVent
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.sykepenger.Gradering
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.sykepenger.Sykmelding
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.sykepenger.Sykmeldingsperiode
import no.nav.modiapersonoversikt.rest.DATOFORMAT
import no.nav.modiapersonoversikt.rest.lagPeriode
import org.joda.time.LocalDate

class SykepengerUttrekk constructor(private val sykepengerService: SykepengerServiceBi) {

    fun hent(fodselsnummer: String): Map<String, Any?> {
        val sykepenger = sykepengerService.hentSykmeldingsperioder(SykepengerRequest(LocalDate.now().minusYears(2), fodselsnummer, LocalDate.now()))

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
                "ferie1" to it.ferie1?.let { ferie -> lagPeriode(ferie) },
                "ferie2" to it.ferie2?.let { ferie -> lagPeriode(ferie) },
                "sanksjon" to it.sanksjon?.let { periode -> lagPeriode(periode) },
                "stansårsak" to it.stansarsak?.termnavn,
                "unntakAktivitet" to it.unntakAktivitet?.termnavn,
                "forsikring" to it.gjeldendeForsikring?.let { forsikring ->
                    mapOf(
                        "forsikringsordning" to forsikring.forsikringsordning,
                        "premiegrunnlag" to forsikring.premiegrunnlag,
                        "erGyldig" to forsikring.erGyldig,
                        "forsikret" to forsikring.forsikret?.let { periode -> lagPeriode(periode) }
                    )
                },
                "sykmeldinger" to it.sykmeldinger?.let { sykemeldinger -> hentSykmeldinger(sykemeldinger) },
                "historiskeUtbetalinger" to it.historiskeUtbetalinger?.let { utbetalinger -> hentHistoriskeUtbetalinger(utbetalinger) },
                "kommendeUtbetalinger" to it.kommendeUtbetalinger?.let { utbetalinger -> hentKommendeUtbetalinger(utbetalinger) },
                "utbetalingerPåVent" to it.utbetalingerPaVent?.let { utbetalinger -> hentUtbetalingerPaVent(utbetalinger) },
                "bruker" to it.bruker?.ident,
                "midlertidigStanset" to it.midlertidigStanset?.toString(DATOFORMAT),
                "slutt" to it.slutt?.toString(DATOFORMAT),
                "arbeidsforholdListe" to it.arbeidsforholdListe?.let { arbeidsforhold -> hentArbeidsgiverForhold(arbeidsforhold) },
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
                "refusjonTom" to it.refusjonTom?.toString(DATOFORMAT),
                "refusjonstype" to it.refusjonstype?.termnavn,
                "sykepengerFom" to it.sykepengerFom?.toString(DATOFORMAT)
            )
        }
    }

    private fun hentSykmeldinger(sykmeldinger: List<Sykmelding>): List<Map<String, Any?>> {
        return sykmeldinger.map {
            mapOf(
                "sykmelder" to it.sykmelder,
                "behandlet" to it.behandlet?.toString(DATOFORMAT),
                "sykmeldt" to it.sykmeldt?.let { periode -> lagPeriode(periode) },
                "sykmeldingsgrad" to it.sykmeldingsgrad,
                "gjelderYrkesskade" to it.gjelderYrkesskade?.let { yrkesskade ->
                    mapOf(
                        "yrkesskadeart" to yrkesskade.yrkesskadeart?.termnavn,
                        "skadet" to yrkesskade.skadet?.toString(DATOFORMAT),
                        "vedtatt" to yrkesskade.vedtatt?.toString(DATOFORMAT)
                    )
                },
                "gradAvSykmeldingListe" to it.gradAvSykmeldingListe?.let { sykemeldinger -> hentGraderinger(sykemeldinger) }
            )
        }
    }

    private fun hentGraderinger(graderinger: List<Gradering>): List<Map<String, Any?>> {
        return graderinger.map {
            mapOf(
                "gradert" to it.gradert?.let { periode -> lagPeriode(periode) },
                "sykmeldingsgrad" to it.sykmeldingsgrad
            )
        }
    }

    private fun hentUtbetalingerPaVent(utbetalingerPaVent: List<UtbetalingPaVent>): List<Map<String, Any?>> {
        return utbetalingerPaVent.map {
            mapOf(
                "vedtak" to it.vedtak?.let { vedtak -> lagPeriode(vedtak) },
                "utbetalingsgrad" to it.utbetalingsgrad,
                "oppgjørstype" to it.oppgjoerstype?.termnavn,
                "arbeidskategori" to it.arbeidskategori?.termnavn,
                "stansårsak" to it.stansaarsak?.termnavn,
                "ferie1" to it.ferie1?.let { ferie -> lagPeriode(ferie) },
                "ferie2" to it.ferie2?.let { ferie -> lagPeriode(ferie) },
                "sanksjon" to it.sanksjon?.let { sanksjon -> lagPeriode(sanksjon) },
                "sykmeldt" to it.sykmeldt?.let { sykmeldt -> lagPeriode(sykmeldt) }
            )
        }
    }
}
