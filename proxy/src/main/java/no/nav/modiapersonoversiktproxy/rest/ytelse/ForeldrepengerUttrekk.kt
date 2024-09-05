package no.nav.modiapersonoversiktproxy.rest.ytelse

import no.nav.modiapersonoversiktproxy.commondomain.Periode
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.Arbeidsforhold
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.foreldrepenger.Adopsjon
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.foreldrepenger.Foedsel
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.foreldrepenger.Foreldrepengeperiode
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.foreldrepenger.Foreldrepengerettighet
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.ForeldrepengerServiceBi
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.mapping.to.ForeldrepengerListeRequest
import no.nav.modiapersonoversiktproxy.rest.JODA_DATOFORMAT
import org.joda.time.LocalDate

class ForeldrepengerUttrekk constructor(private val forelderpengerService: ForeldrepengerServiceBi) {
    fun hent(
        fnr: String,
        start: LocalDate?,
        slutt: LocalDate?,
    ): Map<String, Any?> {
        val foreldrepenger =
            forelderpengerService.hentForeldrepengerListe(
                ForeldrepengerListeRequest(
                    fnr,
                    Periode(start ?: LocalDate.now().minusYears(2), slutt ?: LocalDate.now()),
                ),
            )

        return mapOf(
            "foreldrepenger" to
                foreldrepenger?.foreldrepengerettighet?.let {
                    val fraInfotrygd = hentFraInfotrygd(it)
                    if (fraInfotrygd.isEmpty()) {
                        null
                    } else {
                        listOf(
                            fraInfotrygd,
                        )
                    }
                },
        )
    }

    private fun hentFraInfotrygd(foreldrepenger: Foreldrepengerettighet): Map<String, Any?> {
        return foreldrepenger.let {
            mapOf(
                "forelder" to it.forelder?.ident,
                "andreForeldersFnr" to it.andreForeldersFnr,
                "antallBarn" to it.antallBarn,
                "barnetsFødselsdato" to it.barnetsFoedselsdato?.toString(JODA_DATOFORMAT),
                "dekningsgrad" to it.dekningsgrad,
                "fedrekvoteTom" to it.fedrekvoteTom?.toString(JODA_DATOFORMAT),
                "mødrekvoteTom" to it.moedrekvoteTom?.toString(JODA_DATOFORMAT),
                "foreldrepengetype" to it.foreldrepengetype?.termnavn,
                "graderingsdager" to it.graderingsdager,
                "restDager" to it.restDager,
                "rettighetFom" to it.rettighetFom?.toString(JODA_DATOFORMAT),
                "eldsteIdDato" to it.eldsteIdDato?.toString(JODA_DATOFORMAT),
                "foreldreAvSammeKjønn" to it.foreldreAvSammeKjoenn?.termnavn,
                "periode" to it.periode?.let { periode -> hentForeldrepengeperioder(periode) },
                "slutt" to it.slutt?.toString(JODA_DATOFORMAT),
                "arbeidsforhold" to it.arbeidsforholdListe?.let { liste -> hentArbeidsforhold(liste) },
                "erArbeidsgiverperiode" to it.erArbeidsgiverperiode,
                "arbeidskategori" to it.arbeidskategori?.termnavn,
                when (it) {
                    is Adopsjon -> "omsorgsovertakelse" to it.omsorgsovertakelse?.toString(JODA_DATOFORMAT)
                    is Foedsel -> "termin" to it.termin?.toString(JODA_DATOFORMAT)
                    else -> throw IllegalArgumentException("Ugyldig foreldrepengetype")
                },
            )
        }
    }

    private fun hentArbeidsforhold(arbeidsforhold: List<Arbeidsforhold>): List<Map<String, Any?>> {
        return arbeidsforhold.map {
            mapOf(
                "arbeidsgiverNavn" to it.arbeidsgiverNavn,
                "arbeidsgiverKontonr" to it.arbeidsgiverKontonr,
                "inntektsperiode" to it.inntektsperiode?.termnavn,
                "inntektForPerioden" to it.inntektForPerioden,
                "sykepengerFom" to it.sykepengerFom?.toString(JODA_DATOFORMAT),
                "refusjonTom" to it.refusjonTom?.toString(JODA_DATOFORMAT),
                "refusjonstype" to it.refusjonstype?.termnavn,
            )
        }
    }

    private fun hentForeldrepengeperioder(foreldrepengeperioder: List<Foreldrepengeperiode>): List<Map<String, Any?>> {
        return foreldrepengeperioder.map {
            mapOf(
                "fødselsnummer" to it.fodselsnummer,
                "harAleneomsorgFar" to it.isHarAleneomsorgFar,
                "harAleneomsorgMor" to it.isHarAleneomsorgMor,
                "arbeidsprosentMor" to it.arbeidsprosentMor,
                "avslagsårsak" to it.avslagsaarsak?.termnavn,
                "avslått" to it.avslaatt?.toString(JODA_DATOFORMAT),
                "disponibelGradering" to it.disponibelGradering,
                "erFedrekvote" to it.isErFedrekvote,
                "forskyvelsesårsak1" to it.forskyvelsesaarsak1?.termnavn,
                "forskyvelsesperiode1" to it.forskyvelsesperiode?.let { periode -> lagPeriode(periode) },
                "forskyvelsesårsak2" to it.forskyvelsesaarsak2?.termnavn,
                "forskyvelsesperiode2" to it.forskyvelsesperiode2?.let { periode -> lagPeriode(periode) },
                "foreldrepengerFom" to it.foreldrepengerFom?.toString(JODA_DATOFORMAT),
                "midlertidigStansDato" to it.midlertidigStansDato?.toString(JODA_DATOFORMAT),
                "erMødrekvote" to it.isErModrekvote,
                "morSituasjon" to it.morSituasjon?.termnavn,
                "rettTilFedrekvote" to it.rettTilFedrekvote?.termnavn,
                "rettTilMødrekvote" to it.isRettTilModrekvote?.termnavn,
                "stansårsak" to it.stansaarsak?.termnavn,
                "historiskeUtbetalinger" to it.historiskeUtbetalinger?.let { utbetalinger -> hentHistoriskeUtbetalinger(utbetalinger) },
                "kommendeUtbetalinger" to it.kommendeUtbetalinger?.let { utbetalinger -> hentKommendeUtbetalinger(utbetalinger) },
            )
        }
    }
}
