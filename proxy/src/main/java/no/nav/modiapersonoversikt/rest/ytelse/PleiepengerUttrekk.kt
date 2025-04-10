package no.nav.modiapersonoversikt.rest.ytelse

import no.nav.modiapersonoversikt.commondomain.Periode
import no.nav.modiapersonoversikt.consumer.ereg.OrganisasjonService
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.pleiepenger.Arbeidsforhold
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.pleiepenger.Pleiepengeperiode
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.pleiepenger.Pleiepengerrettighet
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.pleiepenger.Vedtak
import no.nav.modiapersonoversikt.consumer.infotrygd.pleiepenger.PleiepengerService
import no.nav.modiapersonoversikt.consumer.infotrygd.pleiepenger.mapping.to.PleiepengerListeRequest
import no.nav.modiapersonoversikt.rest.DATOFORMAT
import org.joda.time.LocalDate
import org.joda.time.Years

class PleiepengerUttrekk constructor(
    private val pleiepengerService: PleiepengerService,
    private val organisasjonService: OrganisasjonService,
) {
    fun hent(
        fnr: String,
        start: LocalDate?,
        slutt: LocalDate?,
    ): PleiepengerResponse {
        val from = start ?: LocalDate.now().minusYears(2)
        val to = slutt ?: LocalDate.now()
        val diff = Years.yearsBetween(from, to)
        val period =
            if (diff.years > 2) {
                Periode(to.minusYears(2), to)
            } else {
                Periode(from, to)
            }
        val pleiepenger =
            pleiepengerService.hentPleiepengerListe(
                PleiepengerListeRequest(
                    fnr,
                    period,
                ),
            )

        return PleiepengerResponse(
            pleiepenger =
                pleiepenger?.pleieepengerettighetListe?.let {
                    if (it.isEmpty()) {
                        null
                    } else {
                        hentPleiepenger(it)
                    }
                },
        )
    }

    private fun hentPleiepenger(pleiepengerettigheter: List<Pleiepengerrettighet>): List<Pleiepenger> =
        pleiepengerettigheter.map {
            Pleiepenger(
                barnet = it.barnet,
                omsorgsperson = it.omsorgsperson,
                andreOmsorgsperson = it.andreOmsorgsperson,
                restDagerFomIMorgen = it.restDagerFOMIMorgen,
                forbrukteDagerTomIDag = it.forbrukteDagerTOMIDag,
                pleiepengedager = it.pleiepengedager,
                restDagerAnvist = it.restDagerAnvist,
                perioder = it.perioder?.let { periode -> hentPleiepengePerioder(periode) },
            )
        }

    private fun hentPleiepengePerioder(perioder: List<Pleiepengeperiode>): List<PleiepengerPeriode> =
        perioder.map {
            PleiepengerPeriode(
                fom = it.fraOgMed?.format(DATOFORMAT),
                antallPleiepengedager = it.antallPleiepengedager,
                arbeidsforhold = it.arbeidsforholdListe?.let { liste -> hentArbeidsforhold(liste) },
                vedtak = it.vedtakListe?.let { liste -> hentVedtak(liste) },
            )
        }

    private fun hentArbeidsforhold(arbeidsforhold: List<Arbeidsforhold>): List<PleiepengerArbeidsforhold> =
        arbeidsforhold.map {
            PleiepengerArbeidsforhold(
                arbeidsgiverNavn =
                    it.arbeidsgiverOrgnr?.let { orgnr ->
                        hentArbeidsgiverNavn(
                            organisasjonService,
                            orgnr,
                        )
                    },
                arbeidsgiverKontonr = it.arbeidsgiverKontonr,
                inntektsperiode = it.inntektsperiode,
                inntektForPerioden = it.inntektForPerioden,
                refusjonTom = it.refusjonTom?.format(DATOFORMAT),
                refusjonstype = it.refusjonstype,
                arbeidsgiverOrgnr = it.arbeidsgiverOrgnr,
                arbeidskategori = it.arbeidskategori,
            )
        }

    private fun hentVedtak(vedtak: List<Vedtak>): List<PleiepengerVedtak> =
        vedtak.map {
            PleiepengerVedtak(
                periode =
                    it.periode?.let { periode ->
                        PleiepengerVedtakPeriode(fom = periode.fraOgMed?.format(DATOFORMAT), tom = periode.tilOgMed?.format(DATOFORMAT))
                    },
                kompensasjonsgrad = it.kompensasjonsgrad,
                utbetalingsgrad = it.utbetalingsgrad,
                anvistUtbetaling = it.anvistUtbetaling?.format(DATOFORMAT),
                bruttobelop = it.bruttoBelop,
                dagsats = it.dagsats,
                pleiepengegrad = it.pleiepengegrad,
            )
        }
}
