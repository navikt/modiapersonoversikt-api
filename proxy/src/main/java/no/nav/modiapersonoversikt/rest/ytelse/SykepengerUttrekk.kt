package no.nav.modiapersonoversikt.rest.ytelse

import no.nav.modiapersonoversikt.commondomain.Periode
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.Arbeidsforhold
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.UtbetalingPaVent
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.sykepenger.Gradering
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.sykepenger.Sykmelding
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.sykepenger.Sykmeldingsperiode
import no.nav.modiapersonoversikt.consumer.infotrygd.sykepenger.SykepengerServiceBi
import no.nav.modiapersonoversikt.consumer.infotrygd.sykepenger.mapping.to.SykepengerRequest
import no.nav.modiapersonoversikt.rest.JODA_DATOFORMAT
import org.joda.time.LocalDate
import org.joda.time.Years

class SykepengerUttrekk constructor(
    private val sykepengerService: SykepengerServiceBi,
) {
    fun hent(
        fnr: String,
        start: LocalDate?,
        slutt: LocalDate?,
    ): SykepengerResponse {
        val from = start ?: LocalDate.now().minusYears(2)
        val to = slutt ?: LocalDate.now()
        val diff = Years.yearsBetween(from, to)
        val period =
            if (diff.years > 2) {
                Periode(to.minusYears(2), to)
            } else {
                Periode(from, to)
            }
        val sykepenger =
            sykepengerService.hentSykmeldingsperioder(
                SykepengerRequest(fnr, period.from, period.to),
            )

        return SykepengerResponse(
            sykepenger = sykepenger?.sykmeldingsperioder?.let { hentSykemeldingsperioder(it) },
        )
    }

    private fun hentSykemeldingsperioder(sykmeldingsperioder: List<Sykmeldingsperiode>): List<Sykepenger> =
        sykmeldingsperioder.map {
            Sykepenger(
                fodselsnummer = it.fodselsnummer,
                sykmeldtFom = it.sykmeldtFom?.toString(JODA_DATOFORMAT),
                forbrukteDager = it.forbrukteDager,
                ferie1 = it.ferie1?.let { period -> toCommonPeriode(period) },
                ferie2 = it.ferie2?.let { period -> toCommonPeriode(period) },
                sanksjon = it.sanksjon?.let { period -> toCommonPeriode(period) },
                stansaarsak = it.stansarsak?.termnavn,
                unntakAktivitet = it.unntakAktivitet?.termnavn,
                forsikring =
                    it.gjeldendeForsikring?.let { forsikring ->
                        GjeldendeForsikring(
                            forsikringsordning = forsikring.forsikringsordning,
                            premiegrunnlag = forsikring.premiegrunnlag,
                            erGyldig = forsikring.erGyldig,
                            forsikret = forsikring.forsikret?.let { period -> toCommonPeriode(period) },
                        )
                    },
                sykmeldinger = it.sykmeldinger?.let { sykemeldinger -> hentSykmeldinger(sykemeldinger) },
                historiskeUtbetalinger = it.historiskeUtbetalinger?.let { utbetalinger -> hentHistoriskeUtbetalinger(utbetalinger) },
                kommendeUtbetalinger = it.kommendeUtbetalinger?.let { utbetalinger -> hentKommendeUtbetalinger(utbetalinger) },
                utbetalingerPaaVent = it.utbetalingerPaVent?.let { utbetalinger -> hentUtbetalingerPaVent(utbetalinger) },
                bruker = it.bruker?.ident,
                midlertidigStanset = it.midlertidigStanset?.toString(JODA_DATOFORMAT),
                slutt = it.slutt?.toString(JODA_DATOFORMAT),
                arbeidsforholdListe = it.arbeidsforholdListe?.let { arbeidsforhold -> hentArbeidsgiverForhold(arbeidsforhold) },
                erArbeidsgiverperiode = it.erArbeidsgiverperiode,
                arbeidskategori = it.arbeidskategori.termnavn,
            )
        }

    private fun hentArbeidsgiverForhold(arbeidsgiverforholdListe: List<Arbeidsforhold>): List<SykmeldingArbeidsforhold> =
        arbeidsgiverforholdListe.map {
            SykmeldingArbeidsforhold(
                arbeidsgiverNavn = it.arbeidsgiverNavn,
                arbeidsgiverKontonr = it.arbeidsgiverKontonr,
                inntektsperiode = it.inntektsperiode.termnavn,
                inntektForPerioden = it.inntektForPerioden,
                refusjonTom = it.refusjonTom?.toString(JODA_DATOFORMAT),
                refusjonstype = it.refusjonstype?.termnavn,
                sykepengerFom = it.sykepengerFom?.toString(JODA_DATOFORMAT),
            )
        }

    private fun hentSykmeldinger(sykmeldinger: List<Sykmelding>): List<SykmeldingItem> =
        sykmeldinger.map {
            SykmeldingItem(
                sykmelder = it.sykmelder,
                behandlet = it.behandlet?.toString(JODA_DATOFORMAT),
                sykmeldt = it.sykmeldt?.let { period -> toCommonPeriode(period) },
                sykmeldingsgrad = it.sykmeldingsgrad,
                gjelderYrkesskade =
                    it.gjelderYrkesskade?.let { yrkesskade ->
                        GjelderYrkesskade(
                            yrkesskadeart = yrkesskade.yrkesskadeart?.termnavn,
                            skadet = yrkesskade.skadet?.toString(JODA_DATOFORMAT),
                            vedtatt = yrkesskade.vedtatt?.toString(JODA_DATOFORMAT),
                        )
                    },
                gradAvSykmeldingListe = it.gradAvSykmeldingListe?.let { sykemeldinger -> hentGraderinger(sykemeldinger) },
            )
        }

    private fun hentGraderinger(graderinger: List<Gradering>): List<GradAvSykmelding> =
        graderinger.map { it ->
            GradAvSykmelding(
                gradert = it.gradert?.let { period -> toCommonPeriode(period) },
                sykmeldingsgrad = it.sykmeldingsgrad,
            )
        }

    private fun hentUtbetalingerPaVent(utbetalingerPaVent: List<UtbetalingPaVent>): List<SykmeldingUtbetalingPaVent> =
        utbetalingerPaVent.map {
            SykmeldingUtbetalingPaVent(
                vedtak = it.vedtak?.let { vedtak -> toCommonPeriode(vedtak) },
                utbetalingsgrad = it.utbetalingsgrad,
                oppgjorstype = it.oppgjoerstype?.termnavn,
                arbeidskategori = it.arbeidskategori?.termnavn,
                stansaarsak = it.stansaarsak?.termnavn,
                ferie1 = it.ferie1?.let { ferie -> toCommonPeriode(ferie) },
                ferie2 = it.ferie2?.let { ferie -> toCommonPeriode(ferie) },
                sanksjon = it.sanksjon?.let { sanksjon -> toCommonPeriode(sanksjon) },
                sykmeldt = it.sykmeldt?.let { sykmeldt -> toCommonPeriode(sykmeldt) },
            )
        }
}
