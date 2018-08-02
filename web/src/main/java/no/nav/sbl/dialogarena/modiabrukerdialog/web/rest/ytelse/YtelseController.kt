package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.ytelse

import no.nav.kjerneinfo.common.domain.Periode
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.DATOFORMAT
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.lagPeriode
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.lagPleiepengePeriode
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeRequest
import no.nav.sykmeldingsperioder.consumer.pleiepenger.PleiepengerService
import no.nav.sykmeldingsperioder.consumer.pleiepenger.mapping.to.PleiepengerListeRequest
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerRequest
import no.nav.sykmeldingsperioder.domain.HistoriskUtbetaling
import no.nav.sykmeldingsperioder.domain.KommendeUtbetaling
import no.nav.sykmeldingsperioder.domain.Kreditortrekk
import no.nav.sykmeldingsperioder.domain.UtbetalingPaVent
import no.nav.sykmeldingsperioder.domain.foreldrepenger.Foreldrepengeperiode
import no.nav.sykmeldingsperioder.domain.pleiepenger.Arbeidsforhold
import no.nav.sykmeldingsperioder.domain.pleiepenger.Pleiepengeperiode
import no.nav.sykmeldingsperioder.domain.pleiepenger.Pleiepengerrettighet
import no.nav.sykmeldingsperioder.domain.pleiepenger.Vedtak
import no.nav.sykmeldingsperioder.domain.sykepenger.Gradering
import no.nav.sykmeldingsperioder.domain.sykepenger.Sykmelding
import no.nav.sykmeldingsperioder.domain.sykepenger.Sykmeldingsperiode
import org.joda.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/ytelse/{fnr}")
@Produces(MediaType.APPLICATION_JSON)
class YtelseController @Inject constructor(private val sykepengerService: SykepengerServiceBi,
                                           private val forelderpengerService: ForeldrepengerServiceBi,
                                           private val pleiepengerService: PleiepengerService,
                                           private val unleashService: UnleashService) {

    @GET
    fun hent(@PathParam("fnr") fødselsnummer: String): Map<String, Any?> {
        check(unleashService.isEnabled(Feature.NYTT_VISITTKORT))

        val pleiepenger = pleiepengerService.hentPleiepengerListe(PleiepengerListeRequest(fødselsnummer))

        val foreldrepenger = forelderpengerService.hentForeldrepengerListe(ForeldrepengerListeRequest(fødselsnummer, Periode(LocalDate.now().minusYears(2), LocalDate.now())))

        val sykepenger = sykepengerService.hentSykmeldingsperioder(SykepengerRequest(LocalDate.now().minusYears(2), fødselsnummer, LocalDate.now()))

        return mapOf(
                "foreldrepenger" to foreldrepenger?.foreldrepengerettighet?.let { mapOf(
                        "forelder" to it.forelder.ident,
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
                ) },
                "sykepenger" to sykepenger?.let { mapOf(
                        "bruker" to it.bruker?.ident,
                        "perioder" to it.sykmeldingsperioder?.let { hentSykemeldingsperioder(it) }
                ) },
                "pleiepenger" to pleiepenger?.pleieepengerettighetListe?.let {
                    if(it.isEmpty()) {
                        null
                    } else {
                        hentPleiepenger(it)
                    }
                }
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
                    "midlertidigStanset" to it.midlertidigStanset?.toString(DATOFORMAT)
            )
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
                    "gradAvSykemeldingListe" to it.gradAvSykmeldingListe?.let { hentGraderinger(it) }
            )
        }
    }

    private fun hentGraderinger(graderinger: List<Gradering>): List<Map<String, Any?>> {
        return graderinger.map {
            mapOf(
                    "gradert" to it.gradert?.let { lagPeriode(it) },
                    "sykemeldingsgrad" to it.sykmeldingsgrad
            )
        }
    }

    private fun hentPleiepenger(pleiepengerettigheter: List<Pleiepengerrettighet>): List<Map<String, Any?>> {
        return pleiepengerettigheter.map {
            mapOf(
                    "barnet" to it.barnet,
                    "omsorgsperson" to it.omsorgsperson,
                    "andreOmsorgsperson" to it.andreOmsorgsperson,
                    "restDagerFomIMorgen" to it.restDagerFOMIMorgen,
                    "forbrukteDagerTomIDag" to it.forbrukteDagerTOMIDag,
                    "pleiepengedager" to it.pleiepengedager,
                    "restDagerAnvist" to it.restDagerAnvist,
                    "perioder" to it.perioder?.let { hentPleiepengePerioder(it) }
            )
        }
    }

    private fun hentPleiepengePerioder(perioder: List<Pleiepengeperiode>): List<Map<String, Any?>> {
        return perioder.map {
            mapOf(
                    "fom" to it.fraOgMed?.format(DateTimeFormatter.ofPattern(DATOFORMAT)),
                    "antallPleiepengedager" to it.antallPleiepengedager,
                    "arbeidsforhold" to it.arbeidsforholdListe?.let { hentArbeidsforhold(it) },
                    "vedtak" to it.vedtakListe?.let { hentVedtak(it) }
            )
        }
    }

    private fun hentArbeidsforhold(arbeidsforhold: List<Arbeidsforhold>): List<Map<String, Any?>> {
        return arbeidsforhold.map {
            mapOf(
                    "arbeidsgiverNavn" to it.arbeidsgiverNavn,
                    "arbeidsgiverKontonr" to it.arbeidsgiverKontonr,
                    "inntektsperiode" to it.inntektsperiode,
                    "inntektForPerioden" to it.inntektForPerioden,
                    "refusjonTom" to it.refusjonTom?.format(DateTimeFormatter.ofPattern(DATOFORMAT)),
                    "refusjonstype" to it.refusjonstype,
                    "arbeidsgiverOrgnr" to it.arbeidsgiverOrgnr,
                    "arbeidskategori" to it.arbeidskategori
            )
        }
    }

    private fun hentVedtak(vedtak: List<Vedtak>): List<Map<String, Any?>> {
        return vedtak.map {
            mapOf(
                    "periode" to it.periode?.let { lagPleiepengePeriode(it) },
                    "kompensasjonsgrad" to it.kompensasjonsgrad,
                    "utbetalingsgrad" to it.utbetalingsgrad,
                    "anvistUtbetaling" to it.anvistUtbetaling?.format(DateTimeFormatter.ofPattern(DATOFORMAT)),
                    "bruttobeløp" to it.bruttoBelop,
                    "dagsats" to it.dagsats,
                    "pleiepengegrad" to it.pleiepengegrad
            )
        }
    }

    private fun hentHistoriskeUtbetalinger(historiskeUtbetalinger: List<HistoriskUtbetaling>): List<Map<String, Any?>> {
        return historiskeUtbetalinger.map {
            mapOf(
                    "vedtak" to it.vedtak?.let { lagPeriode(it) },
                    "utbetalingsgrad" to it.utbetalingsgrad,
                    "utbetalingsdato" to it.utbetalingsdato?.toString(DATOFORMAT),
                    "nettobeløp" to it.nettobelop,
                    "bruttobeløp" to it.bruttobeloep,
                    "skattetrekk" to it.skattetrekk,
                    "arbeidsgiverNavn" to it.arbeidsgiverNavn,
                    "arbeidsgiverOrgNr" to it.arbeidsgiverOrgNr,
                    "dagsats" to it.dagsats,
                    "type" to it.type,
                    "trekk" to it.trekk?.let { hentKreditorTrekk(it) }
            )
        }
    }

    private fun hentKommendeUtbetalinger(kommendeUtbetalinger: List<KommendeUtbetaling>): List<Map<String, Any?>> {
        return kommendeUtbetalinger.map {
            mapOf(
                    "vedtak" to it.vedtak?.let { lagPeriode(it) },
                    "utbetalingsgrad" to it.utbetalingsgrad,
                    "utbetalingsdato" to it.utbetalingsdato?.toString(DATOFORMAT),
                    "bruttobeløp" to it.bruttobeloep,
                    "arbeidsgiverNavn" to it.arbeidsgiverNavn,
                    "arbeidsgiverKontonr" to it.arbeidsgiverKontonr,
                    "arbeidsgiverOrgNr" to it.arbeidsgiverOrgnr,
                    "dagsats" to it.dagsats,
                    "saksbehandler" to it.saksbehandler,
                    "type" to it.type?.termnavn
            )
        }
    }

    private fun hentKreditorTrekk(kreditortrekk: List<Kreditortrekk>): List<Map<String, Any?>> {
        return kreditortrekk.map {
            mapOf(
                    "kreditorsNavn" to it.kreditorsNavn,
                    "beløp" to it.belop
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