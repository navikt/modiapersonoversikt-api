package no.nav.modiapersonoversikt.rest.ytelse

import no.nav.modiapersonoversikt.commondomain.Periode
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.Arbeidsforhold
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.foreldrepenger.Adopsjon
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.foreldrepenger.Foedsel
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.foreldrepenger.Foreldrepengeperiode
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.foreldrepenger.Foreldrepengerettighet
import no.nav.modiapersonoversikt.consumer.infotrygd.foreldrepenger.ForeldrepengerServiceBi
import no.nav.modiapersonoversikt.consumer.infotrygd.foreldrepenger.mapping.to.ForeldrepengerListeRequest
import no.nav.modiapersonoversikt.infotrgd.foreldrepenger.ForeldrepengePeriode
import no.nav.modiapersonoversikt.infotrgd.foreldrepenger.Foreldrepenger
import no.nav.modiapersonoversikt.infotrgd.foreldrepenger.ForeldrepengerArbeidsforhold
import no.nav.modiapersonoversikt.infotrgd.foreldrepenger.ForeldrepengerResponse
import no.nav.modiapersonoversikt.rest.JODA_DATOFORMAT
import org.joda.time.LocalDate
import org.joda.time.Years

class ForeldrepengerUttrekk(
    private val forelderpengerService: ForeldrepengerServiceBi,
) {
    fun hent(
        fnr: String,
        start: LocalDate?,
        slutt: LocalDate?,
    ): ForeldrepengerResponse {
        val from = start ?: LocalDate.now().minusYears(2)
        val to = slutt ?: LocalDate.now()
        val diff = Years.yearsBetween(from, to)
        val period =
            if (diff.years > 2) {
                Periode(to.minusYears(2), to)
            } else {
                Periode(from, to)
            }

        val foreldrepenger =
            forelderpengerService.hentForeldrepengerListe(ForeldrepengerListeRequest(fnr, period))

        return ForeldrepengerResponse(
            foreldrepenger =
                foreldrepenger?.foreldrepengerettighet?.let {
                    val fraInfotrygd = hentFraInfotrygd(it)
                    fraInfotrygd?.let {
                        listOf(
                            it,
                        )
                    }
                },
        )
    }

    private fun hentFraInfotrygd(foreldrepenger: Foreldrepengerettighet): Foreldrepenger? {
        val commonForeldrepenger =
            Foreldrepenger(
                forelder = foreldrepenger.forelder?.ident,
                andreForeldersFnr = foreldrepenger.andreForeldersFnr,
                antallBarn = foreldrepenger.antallBarn,
                barnetsFodselsdato = foreldrepenger.barnetsFoedselsdato?.toString(JODA_DATOFORMAT),
                dekningsgrad = foreldrepenger.dekningsgrad,
                fedrekvoteTom = foreldrepenger.fedrekvoteTom?.toString(JODA_DATOFORMAT),
                modrekvoteTom = foreldrepenger.moedrekvoteTom?.toString(JODA_DATOFORMAT),
                foreldrepengetype = foreldrepenger.foreldrepengetype?.termnavn,
                graderingsdager = foreldrepenger.graderingsdager,
                restDager = foreldrepenger.restDager,
                rettighetFom = foreldrepenger.rettighetFom?.toString(JODA_DATOFORMAT),
                eldsteIdDato = foreldrepenger.eldsteIdDato?.toString(JODA_DATOFORMAT),
                foreldreAvSammeKjonn = foreldrepenger.foreldreAvSammeKjoenn?.termnavn,
                periode = foreldrepenger.periode?.let { periode -> hentForeldrepengeperioder(periode) },
                slutt = foreldrepenger.slutt?.toString(JODA_DATOFORMAT),
                arbeidsforhold = foreldrepenger.arbeidsforholdListe?.let { liste -> hentArbeidsforhold(liste) },
                erArbeidsgiverperiode = foreldrepenger.erArbeidsgiverperiode,
                arbeidskategori = foreldrepenger.arbeidskategori?.termnavn,
                omsorgsovertakelse = null,
                termin = null,
            )

        val foreldrePenger =
            when (foreldrepenger) {
                is Adopsjon ->
                    commonForeldrepenger.copy(
                        omsorgsovertakelse = foreldrepenger.omsorgsovertakelse?.toString(JODA_DATOFORMAT),
                    )

                is Foedsel ->
                    commonForeldrepenger.copy(
                        termin = foreldrepenger.termin?.toString(JODA_DATOFORMAT),
                    )

                else -> null
            }

        return foreldrePenger
    }

    private fun hentArbeidsforhold(arbeidsforhold: List<Arbeidsforhold>): List<ForeldrepengerArbeidsforhold> =
        arbeidsforhold.map {
            ForeldrepengerArbeidsforhold(
                arbeidsgiverNavn = it.arbeidsgiverNavn,
                arbeidsgiverKontonr = it.arbeidsgiverKontonr,
                inntektsperiode = it.inntektsperiode?.termnavn,
                inntektForPerioden = it.inntektForPerioden,
                sykepengerFom = it.sykepengerFom?.toString(JODA_DATOFORMAT),
                refusjonTom = it.refusjonTom?.toString(JODA_DATOFORMAT),
                refusjonstype = it.refusjonstype?.termnavn,
            )
        }

    private fun hentForeldrepengeperioder(foreldrepengeperioder: List<Foreldrepengeperiode>): List<ForeldrepengePeriode> =
        foreldrepengeperioder.map {
            ForeldrepengePeriode(
                fodselsnummer = it.fodselsnummer,
                harAleneomsorgFar = it.isHarAleneomsorgFar,
                harAleneomsorgMor = it.isHarAleneomsorgMor,
                arbeidsprosentMor = it.arbeidsprosentMor,
                avslagsaarsak = it.avslagsaarsak?.termnavn,
                avslaatt = it.avslaatt?.toString(JODA_DATOFORMAT),
                disponibelGradering = it.disponibelGradering,
                erFedrekvote = it.isErFedrekvote,
                forskyvelsesaarsak1 = it.forskyvelsesaarsak1?.termnavn,
                forskyvelsesperiode1 = it.forskyvelsesperiode?.let { periode -> toCommonPeriode(periode) },
                forskyvelsesaarsak2 = it.forskyvelsesaarsak2?.termnavn,
                forskyvelsesperiode2 = it.forskyvelsesperiode2?.let { periode -> toCommonPeriode(periode) },
                foreldrepengerFom = it.foreldrepengerFom?.toString(JODA_DATOFORMAT),
                midlertidigStansDato = it.midlertidigStansDato?.toString(JODA_DATOFORMAT),
                erModrekvote = it.isErModrekvote,
                morSituasjon = it.morSituasjon?.termnavn,
                rettTilFedrekvote = it.rettTilFedrekvote?.termnavn,
                rettTilModrekvote = it.isRettTilModrekvote?.termnavn,
                stansaarsak = it.stansaarsak?.termnavn,
                historiskeUtbetalinger =
                    it.historiskeUtbetalinger?.let { utbetalinger ->
                        hentHistoriskeUtbetalinger(
                            utbetalinger,
                        )
                    },
                kommendeUtbetalinger =
                    it.kommendeUtbetalinger?.let { utbetalinger ->
                        hentKommendeUtbetalinger(
                            utbetalinger,
                        )
                    },
            )
        }
}
