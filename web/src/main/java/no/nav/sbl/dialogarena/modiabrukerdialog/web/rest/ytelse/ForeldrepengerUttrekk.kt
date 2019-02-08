package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.ytelse

import no.nav.kjerneinfo.common.domain.Periode
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.DATOFORMAT
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.lagPeriode
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeRequest
import no.nav.sykmeldingsperioder.domain.foreldrepenger.Foreldrepengeperiode
import org.joda.time.LocalDate
import java.util.*

class ForeldrepengerUttrekk constructor(private val forelderpengerService: ForeldrepengerServiceBi) {

    fun hent(fødselsnummer: String): List<Map<String, Any?>> {
        return Arrays.asList(hentFraInfotrygd(fødselsnummer))
    }


    private fun hentFraInfotrygd(fødselsnummer: String): Map<String, Any?> {
        val foreldrepenger = forelderpengerService.hentForeldrepengerListe(ForeldrepengerListeRequest(fødselsnummer, Periode(LocalDate.now().minusYears(2), LocalDate.now())))


        return mapOf(
                "foreldrepenger" to foreldrepenger?.foreldrepengerettighet?.let { mapOf(
                        "forelder" to it.forelder?.ident,
                        "andreForeldersFnr" to it.andreForeldersFnr,
                        "antallBarn" to it.antallBarn,
                        "barnetsFødselsdato" to it.barnetsFoedselsdato?.toString(DATOFORMAT),
                        "dekningsgrad" to it.dekningsgrad,
                        "fedrekvoteTom" to it.fedrekvoteTom?.toString(DATOFORMAT),
                        "mødrekvoteTom" to it.moedrekvoteTom?.toString(DATOFORMAT),
                        "foreldrepengetype" to it.foreldrepengetype?.termnavn,
                        "graderingsdager" to it.graderingsdager,
                        "restDager" to it.restDager,
                        "rettighetFom" to it.rettighetFom?.toString(DATOFORMAT),
                        "eldsteIdDato" to it.eldsteIdDato?.toString(DATOFORMAT),
                        "foreldreAvSammeKjønn" to it.foreldreAvSammeKjoenn?.termnavn,
                        "periode" to it.periode?.let { hentForeldrepengeperioder(it) }
                ) }
        )
    }

    private fun hentForeldrepengeperioder(foreldrepengeperioder: List<Foreldrepengeperiode>): List<Map<String, Any?>> {
        return foreldrepengeperioder.map {
            mapOf(
                    "fødselsnummer" to it.fodselsnummer,
                    "harAleneomsorgFar" to it.isHarAleneomsorgFar,
                    "harAleneomsorgMor" to it.isHarAleneomsorgMor,
                    "arbeidsprosentMor" to it.arbeidsprosentMor,
                    "avslagsårsak" to it.avslagsaarsak?.termnavn,
                    "avslått" to it.avslaatt?.toString(DATOFORMAT),
                    "disponibelGradering" to it.disponibelGradering,
                    "erFedrekvote" to it.isErFedrekvote,
                    "forskyvelsesårsak1" to it.forskyvelsesaarsak1?.termnavn,
                    "forskyvelsesperiode1" to it.forskyvelsesperiode?.let { lagPeriode(it) },
                    "forskyvelsesårsak2" to it.forskyvelsesaarsak2?.termnavn,
                    "forskyvelsesperiode2" to it.forskyvelsesperiode2?.let { lagPeriode(it) },
                    "foreldrepengerFom" to it.foreldrepengerFom?.toString(DATOFORMAT),
                    "midlertidigStansDato" to it.midlertidigStansDato?.toString(DATOFORMAT),
                    "erMødrekvote" to it.isErModrekvote,
                    "morSituasjon" to it.morSituasjon?.termnavn,
                    "rettTilFedrekvote" to it.rettTilFedrekvote?.termnavn,
                    "rettTilMødrekvote" to it.isRettTilModrekvote?.termnavn,
                    "stansårsak" to it.stansaarsak?.termnavn,
                    "historiskeUtbetalinger" to it.historiskeUtbetalinger?.let { hentHistoriskeUtbetalinger(it) },
                    "kommendeUtbetalinger" to it.kommendeUtbetalinger?.let { hentKommendeUtbetalinger(it) }
            )
        }
    }

}