package no.nav.modiapersonoversiktproxy.rest.ytelse

import no.nav.modiapersonoversiktproxy.commondomain.Periode
import no.nav.modiapersonoversiktproxy.consumer.ereg.OrganisasjonService
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.pleiepenger.Arbeidsforhold
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.pleiepenger.Pleiepengeperiode
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.pleiepenger.Pleiepengerrettighet
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.pleiepenger.Vedtak
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.pleiepenger.PleiepengerService
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.pleiepenger.mapping.to.PleiepengerListeRequest
import no.nav.modiapersonoversiktproxy.rest.DATOFORMAT
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
    ): Map<String, Any?> {
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

        return mapOf(
            "pleiepenger" to
                pleiepenger?.pleieepengerettighetListe?.let {
                    if (it.isEmpty()) {
                        null
                    } else {
                        hentPleiepenger(it)
                    }
                },
        )
    }

    private fun hentPleiepenger(pleiepengerettigheter: List<Pleiepengerrettighet>): List<Map<String, Any?>> =
        pleiepengerettigheter.map {
            mapOf(
                "barnet" to it.barnet,
                "omsorgsperson" to it.omsorgsperson,
                "andreOmsorgsperson" to it.andreOmsorgsperson,
                "restDagerFomIMorgen" to it.restDagerFOMIMorgen,
                "forbrukteDagerTomIDag" to it.forbrukteDagerTOMIDag,
                "pleiepengedager" to it.pleiepengedager,
                "restDagerAnvist" to it.restDagerAnvist,
                "perioder" to it.perioder?.let { perioder -> hentPleiepengePerioder(perioder) },
            )
        }

    private fun hentPleiepengePerioder(perioder: List<Pleiepengeperiode>): List<Map<String, Any?>> =
        perioder.map {
            mapOf(
                "fom" to it.fraOgMed?.format(DATOFORMAT),
                "antallPleiepengedager" to it.antallPleiepengedager,
                "arbeidsforhold" to it.arbeidsforholdListe?.let { liste -> hentArbeidsforhold(liste) },
                "vedtak" to it.vedtakListe?.let { liste -> hentVedtak(liste) },
            )
        }

    private fun hentArbeidsforhold(arbeidsforhold: List<Arbeidsforhold>): List<Map<String, Any?>> =
        arbeidsforhold.map {
            mapOf(
                "arbeidsgiverNavn" to
                    it.arbeidsgiverOrgnr?.let { orgnr ->
                        hentArbeidsgiverNavn(
                            organisasjonService,
                            orgnr,
                        )
                    },
                "arbeidsgiverKontonr" to it.arbeidsgiverKontonr,
                "inntektsperiode" to it.inntektsperiode,
                "inntektForPerioden" to it.inntektForPerioden,
                "refusjonTom" to it.refusjonTom?.format(DATOFORMAT),
                "refusjonstype" to it.refusjonstype,
                "arbeidsgiverOrgnr" to it.arbeidsgiverOrgnr,
                "arbeidskategori" to it.arbeidskategori,
            )
        }

    private fun hentVedtak(vedtak: List<Vedtak>): List<Map<String, Any?>> =
        vedtak.map {
            mapOf(
                "periode" to it.periode?.let { periode -> lagPleiepengePeriode(periode) },
                "kompensasjonsgrad" to it.kompensasjonsgrad,
                "utbetalingsgrad" to it.utbetalingsgrad,
                "anvistUtbetaling" to it.anvistUtbetaling?.format(DATOFORMAT),
                "bruttobeløp" to it.bruttoBelop,
                "dagsats" to it.dagsats,
                "pleiepengegrad" to it.pleiepengegrad,
            )
        }
}
